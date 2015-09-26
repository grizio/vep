package vep.service.session

import anorm.SqlParser._
import anorm._
import vep.AnormClient
import vep.exception.FieldStructuredErrorException
import vep.model.common.{ErrorCodes, ErrorItem, ErrorItemTree}
import vep.model.session._
import vep.model.theater.Theater
import vep.service.{AnormImplicits, VepServicesComponent}
import vep.utils.{DB, StringUtils}

trait ReservationServiceComponent {
  def reservationService: ReservationService

  trait ReservationService {
    /**
     * Creates a new reservation and returns its id and key.
     * @param reservationForm The data of reservation to create
     * @return The id and key of the created reservation
     */
    def create(reservationForm: ReservationForm): ReservationFormResult

    /**
     * Finds a reservation by its id.
     * @param id The reservation id
     * @return The reservation with given id if exist
     */
    def find(id: Int): Option[ReservationDetail]

    /**
     * Finds the list of reservation from given session
     * @param id The session id
     * @return The list of reservation from session
     */
    def findFromSession(id: Int): Seq[ReservationDetail]
  }

}

trait ReservationServiceProductionComponent extends ReservationServiceComponent {
  self: AnormClient with VepServicesComponent =>

  lazy val reservationService: ReservationService = new ReservationServiceProduction

  class ReservationServiceProduction extends ReservationService with AnormImplicits {
    override def create(reservationForm: ReservationForm): ReservationFormResult = DB.withTransaction { implicit c =>
      lazy val theaterOpt = theaterService.findByCanonical(reservationForm.theater)

      lazy val idSessionOpt = SQL("SELECT id FROM session WHERE theater = {theater} AND canonical = {canonical} FOR UPDATE")
        .on("theater" -> theaterOpt.get.id)
        .on("canonical" -> reservationForm.session)
        .as(scalar[Int].singleOpt)

      theaterOpt match {
        case Some(theater) =>
          idSessionOpt match {
            case Some(idSession) =>
              checkSeats(theater, idSession, reservationForm.seats, reservationForm.seatList)
              checkPrice(idSession, reservationForm.prices)

              val pass = StringUtils.generateSalt()

              val id = SQL( """
                              |INSERT INTO reservation(session, firstname, lastname, email, city, comment, seats, pass)
                              |VALUES ({session}, {firstname}, {lastname}, {email}, {city}, {comment}, {seats}, {pass})
                            """.stripMargin)
                .on("session" -> idSession)
                .on("firstname" -> reservationForm.firstName)
                .on("lastname" -> reservationForm.lastName)
                .on("email" -> reservationForm.email)
                .on("city" -> reservationForm.city)
                .on("comment" -> reservationForm.comment)
                .on("seats" -> reservationForm.seats)
                .on("pass" -> pass)
                .executeInsert[Int](scalar[Int].single)

              reservationForm.seatList foreach { seat =>
                SQL(
                  """
                    |INSERT INTO reservation_seat(reservation, seat)
                    |VALUES ({reservation}, {seat})
                  """.stripMargin)
                  .on("reservation" -> id)
                  .on("seat" -> seat)
                  .executeInsert()
              }

              reservationForm.prices foreach { price =>
                SQL(
                  """
                    |INSERT INTO reservation_price(reservation, price, number)
                    |VALUES ({reservation}, {price}, {number})
                  """.stripMargin)
                  .on("reservation" -> id)
                  .on("price" -> price.price)
                  .on("number" -> price.number)
                  .executeInsert()
              }

              ReservationFormResult(id, pass)
            case None =>
              throw new FieldStructuredErrorException(
                "The session does not exist",
                ErrorItem.build("session" -> ErrorCodes.undefinedSession)
              )
          }
        case None =>
          throw new FieldStructuredErrorException(
            "The theater does not exist",
            ErrorItem.build("theater" -> ErrorCodes.undefinedTheater)
          )
      }
    }

    def checkSeats(theater: Theater, session: Int, seats: Option[Int], seatList: Seq[String]) = {
      if (theater.fixed) {
        checkSeatList(theater, session, seatList)
      } else {
        if (seats.isEmpty) {
          throw new FieldStructuredErrorException(
            "The number of seats is undefined",
            ErrorItem.build("seats" -> ErrorCodes.emptyField)
          )
        } else if (seats.get <= 0) {
          throw new FieldStructuredErrorException(
            "The number of reserved seats is negative or null",
            ErrorItem.build("seats" -> ErrorCodes.negativeOrNull)
          )
        }
      }
    }

    def checkSeatList(theater: Theater, session: Int, seatList: Seq[String]) = {
      if (seatList.isEmpty) {
        throw new FieldStructuredErrorException(
          "The number of seats is undefined",
          ErrorItem.build("seatList" -> ErrorCodes.emptyField)
        )
      } else {
        val seatExist = seatList.map(theaterService.containsSeat(theater.canonical, _))
        if (seatExist.contains(false)) {
          throw new FieldStructuredErrorException(
            "At least one seat is invalid",
            ErrorItemTree(Map("seatList" -> ErrorItem.seqToErrorItemSeq(seatExist, ErrorCodes.undefinedSeat)(!_)))
          )
        } else {
          val seatTaken = seatList.map(isSeatTaken(session, _))
          if (seatTaken.contains(true)) {
            throw new FieldStructuredErrorException(
              "At least one seat is taken",
              ErrorItemTree(Map("seatList" -> ErrorItem.seqToErrorItemSeq(seatTaken, ErrorCodes.reservedSeat)(_ == true)))
            )
          }
        }
      }
    }

    def checkPrice(idSession: Int, prices: Seq[ReservationPriceForm]) = {
      val priceExist = prices map { price =>
        sessionService.priceExists(price.price, Some(idSession))
      }

      if (priceExist.contains(false)) {
        throw new FieldStructuredErrorException(
          "At least one price is invalid",
          ErrorItemTree(Map("prices" -> ErrorItem.seqToErrorItemSeq(priceExist, ErrorCodes.undefinedPrice)(!_)))
        )
      }
    }

    def isSeatTaken(session: Int, seat: String): Boolean = DB.withConnection { implicit c =>
      val count = SQL(
        """
          |SELECT count(*)
          |FROM reservation_seat rs
          |JOIN reservation r ON rs.reservation = r.id
          |JOIN session s ON r.session = s.id
          |WHERE s.id = {session}
          |AND rs.seat = {seat}
        """.stripMargin)
        .on("session" -> session)
        .on("seat" -> seat)
        .as(scalar[Int].single)

      count == 1
    }

    override def find(id: Int): Option[ReservationDetail] = DB.withConnection { implicit c =>
      val reservationDetailOpt = SQL(
        """SELECT r.id, s.canonical, r.firstName, r.lastName, r.email, r.city, r.comment, r.seats, r.pass
          | FROM reservation r
          | JOIN session s ON s.id = r.session
          | WHERE r.id = {id}""".stripMargin)
        .on("id" -> id)
        .as(ReservationParsers.reservationDetail.singleOpt)

      reservationDetailOpt.map(populateReservationChildren)
    }

    override def findFromSession(id: Int): Seq[ReservationDetail] = DB.withConnection { implicit c =>
      val reservationDetailSeq = SQL(
        """SELECT r.id, s.canonical, r.firstName, r.lastName, r.email, r.city, r.comment, r.seats, r.pass
          | FROM reservation r
          | JOIN session s ON s.id = r.session
          | WHERE s.id = {id}""".stripMargin)
        .on("id" -> id)
        .as(ReservationParsers.reservationDetail *)

      reservationDetailSeq.map(populateReservationChildren)
    }

    def populateReservationChildren(reservationDetail: ReservationDetail): ReservationDetail = DB.withConnection { implicit c =>
        val reservationSeats = SQL("SELECT seat FROM reservation_seat WHERE reservation = {reservation}")
          .on("reservation" -> reservationDetail.id)
          .as(scalar[String] *)

        val reservationPrices = SQL(
          """
            |SELECT rp.price, rp.number, sp.price
            |FROM reservation_price rp
            |JOIN session_price sp ON rp.price = sp.id
            |WHERE rp.reservation = {reservation}""".stripMargin)
          .on("reservation" -> reservationDetail.id)
          .as(ReservationParsers.reservationPriceDetail *)

        reservationDetail.copy(
          seatList = reservationSeats,
          prices = reservationPrices
        )
    }
  }

}
