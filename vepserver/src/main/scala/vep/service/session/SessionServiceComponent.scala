package vep.service.session

import java.sql.Connection

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
import vep.AnormClient
import vep.exception.FieldStructuredErrorException
import vep.model.common._
import vep.model.session.{SessionDetail, SessionForm, SessionPriceForm, SessionUpdateForm}
import vep.model.show.Show
import vep.model.theater.Theater
import vep.service.{AnormImplicits, VepServicesComponent}
import vep.utils.{DB, DateUtils, StringUtils}

trait SessionServiceComponent {
  def sessionService: SessionService

  trait SessionService {
    /**
     * Creates a new session in database.
     * @param sessionForm The session to create
     * @return The canonical of the new session
     */
    def create(sessionForm: SessionForm): String

    /**
     * UPdates an existing session from database
     * @param sessionUpdateForm The session to update
     */
    def update(sessionUpdateForm: SessionUpdateForm)

    /**
     * Checks if the session with given theater and canonical exists.
     * @param theater The theater canonical
     * @param session The session canonical
     * @return true if the session exists, otherwise false.
     */
    def exists(theater: String, session: String): Boolean

    /**
     * Finds the detail of a session
     * @param theater The theater canonical
     * @param session The session canonical
     * @return The detail of the session if exists
     */
    def findDetail(theater: String, session: String): Option[SessionDetail]

    /**
     * Counts the number of future sessions for given theater.
     * @param theater The theater canonical
     * @return The number of sessions
     */
    def countByTheater(theater: String): Int

    /**
     * Checks if current id exists exists.
     * @param id The price id
     * @param session If given, check only for given session
     * @return true if the price exists, otherwise false
     */
    def priceExists(id: Int, session: Option[Int] = None): Boolean
  }

}

trait SessionServiceProductionComponent extends SessionServiceComponent {
  self: AnormClient with VepServicesComponent =>

  lazy val sessionService = new SessionServiceProduction

  class SessionServiceProduction extends SessionService with AnormImplicits {
    override def create(sessionForm: SessionForm): String = DB.withTransaction { implicit c =>
      theaterService.findByCanonical(sessionForm.theater) match {
        case Some(theater) =>
          val shows = sessionForm.shows map { show => showService.findByCanonical(show) }
          if (shows forall { s => s.isDefined }) {
            if (dateNotExistsForTheater(theater, sessionForm.dtDate)) {
              val canonical = nextCanonical(theater.id, sessionForm.name)
              val sessionID = insertSession(sessionForm, theater, canonical)
              sessionForm.prices foreach { price => insertSessionPrice(sessionID, price) }
              shows.foldLeft(1) { (i, show) =>
                insertSessionShow(sessionID, i, show)
                i + 1
              }
              canonical
            } else {
              throw new FieldStructuredErrorException(
                "The date already exist for this theater",
                ErrorItem.build("date" -> ErrorCodes.existingDateForTheater)
              )
            }
          } else {
            throw new FieldStructuredErrorException(
              "At least one show is invalid",
              ErrorItemTree(Map("shows" -> ErrorItem.seqToErrorItemSeq(shows, ErrorCodes.undefinedShow) { show => show.isEmpty }))
            )
          }
        case None => throw new FieldStructuredErrorException(
          "The theater does not exist.",
          ErrorItem.build("theater" -> ErrorCodes.undefinedTheater)
        )
      }
    }

    override def update(sessionUpdateForm: SessionUpdateForm): Unit = DB.withTransaction { implicit c =>
      val oldSessionOpt = sessionService.findDetail(sessionUpdateForm.theater, sessionUpdateForm.session)
      oldSessionOpt match {
        case Some(oldSession) =>
          val shows = sessionUpdateForm.shows map { show => showService.findByCanonical(show) }
          if (shows forall { s => s.isDefined }) {
            if (!oldSession.date.isEqual(sessionUpdateForm.dtDate) && sessionUpdateForm.dtDate.isBeforeNow) {
              throw new FieldStructuredErrorException(
                "The session date has change and is not in the future",
                ErrorItem.build("date" -> ErrorCodes.dateTooSoon)
              )
            } else if (!oldSession.reservationEndDate.isEqual(sessionUpdateForm.dtReservationEndDate)) {
              if (sessionUpdateForm.dtReservationEndDate.isBeforeNow) {
                throw new FieldStructuredErrorException(
                  "The The end date of reservation has change and is not in the future",
                  ErrorItem.build("reservationEndDate" -> ErrorCodes.dateTooSoon)
                )
              }
            }

            val sessionID = SQL("SELECT id FROM session WHERE theater IN (SELECT id FROM theater WHERE canonical = {theater}) AND canonical = {canonical}")
              .on("theater" -> sessionUpdateForm.theater)
              .on("canonical" -> sessionUpdateForm.session)
              .as(scalar[Long].single)

            SQL(
              """
                |UPDATE session SET
                |date = {date},
                |name = {name},
                |reservationEndDate = {reservationEndDate}
                |WHERE id = {id}
              """.
                stripMargin)
              .on("date" -> DateUtils.toStringSQL(sessionUpdateForm.dtDate))
              .on("name" -> sessionUpdateForm.name)
              .on("reservationEndDate" -> DateUtils.toStringSQL(sessionUpdateForm.dtReservationEndDate))
              .on("id" -> sessionID)
              .executeUpdate()

            SQL("DELETE FROM session_price WHERE session = {session}")
              .on("session" -> sessionID)
              .executeUpdate()

            sessionUpdateForm.prices foreach { p => insertSessionPrice(sessionID, p) }

            SQL("DELETE FROM session_show WHERE session = {session}")
              .on("session" -> sessionID)
              .executeUpdate()

            shows.foldLeft(1) { (i, show) =>
              insertSessionShow(sessionID, i, show)
              i + 1
            }
          } else {
            throw new FieldStructuredErrorException(
              "At least one show is invalid",
              ErrorItemTree(Map("shows" -> ErrorItem.seqToErrorItemSeq(shows, ErrorCodes.undefinedShow) { show => show.isEmpty }))
            )
          }
        case None => throw new FieldStructuredErrorException(
          "The session does not exist",
          ErrorItem.build("session" -> ErrorCodes.undefinedSession)
        )
      }
    }

    override def exists(theater: String, session: String): Boolean = DB.withConnection { implicit c =>
      val n = SQL(
        """
          |SELECT COUNT(*)
          |FROM session s
          |JOIN theater t ON s.theater = t.id
          |WHERE t.canonical = {theater}
          |AND s.canonical = {session}
        """.stripMargin)
        .on("theater" -> theater)
        .on("session" -> session)
        .as(scalar[Int].single)
      n != 0
    }

    override def findDetail(theater: String, session: String): Option[SessionDetail] = DB.withConnection { implicit c =>
      val sessionDetailOpt = SQL(
        """
          |SELECT s.id, t.canonical, s.canonical, s.date, s.name, s.reservationEndDate
          |FROM session s
          |JOIN theater t ON t.id = s.theater
          |WHERE t.canonical = {theater}
          |AND s.canonical = {session}
        """.stripMargin)
        .on("theater" -> theater)
        .on("session" -> session)
        .as(SessionParsers.sessionDetailParser.singleOpt)

      sessionDetailOpt map { sessionDetail =>
        val prices = SQL(
          """
            |SELECT name, price, cases
            |FROM session_price
            |WHERE session = {session}
          """.stripMargin)
          .on("session" -> sessionDetail.id)
          .as(SessionParsers.sessionPriceDetailParser *)

        val shows = SQL(
          """
            |SELECT s.canonical
            |FROM   session_show ss
            |JOIN   shows s ON ss.shows = s.id
            |WHERE  session = {session}
            |ORDER BY num ASC""".stripMargin)
          .on("session" -> sessionDetail.id)
          .as(scalar[String] *)

        sessionDetail.toSessionDetail(prices, shows)
      }
    }

    def dateNotExistsForTheater(theater: Theater, date: DateTime): Boolean = DB.withConnection { implicit c =>
      val n = SQL("SELECT COUNT(*) FROM session WHERE theater = {theater} AND date = {date}")
        .on("theater" -> theater.id)
        .on("date" -> DateUtils.toStringSQL(date))
        .as(scalar[Int].single)
      n == 0
    }

    def nextCanonical(theater: Long, name: String): String = DB.withConnection { implicit c =>
      val canonical = StringUtils.canonicalize(name)
      val canonicals = SQL("SELECT canonical FROM session WHERE theater = {theater} AND canonical REGEXP {canonical} = 1")
        .on("theater" -> theater)
        .on("canonical" -> s"^${canonical}_[0-9]+$$")
        .as(scalar[String] *)
      val numbers = canonicals map { c => c.substring(c.lastIndexOf('_') + 1).toInt }
      val n = if (numbers.isEmpty) 1 else numbers.max + 1
      s"${canonical}_$n"
    }

    def insertSession(sessionForm: SessionForm, theater: Theater, canonical: String)(implicit c: Connection): Long = {
      SQL(
        """INSERT INTO session(theater, canonical, date, name, reservationEndDate)
          |VALUES ({theater}, {canonical}, {date}, {name}, {reservationEndDate})
        """.stripMargin)
        .on("theater" -> theater.id)
        .on("canonical" -> canonical)
        .on("date" -> sessionForm.date)
        .on("name" -> sessionForm.name)
        .on("reservationEndDate" -> sessionForm.reservationEndDate)
        .executeInsert[Long](scalar[Long].single)
    }

    def insertSessionPrice(sessionID: Long, price: SessionPriceForm)(implicit c: Connection): Int = {
      SQL(
        """INSERT INTO session_price(session, name, price, cases)
          |VALUES({session}, {name}, {price}, {cases})
        """.stripMargin)
        .on("session" -> sessionID)
        .on("name" -> price.name)
        .on("price" -> price.price)
        .on("cases" -> price.condition)
        .executeUpdate()
    }

    def insertSessionShow(sessionID: Long, i: Int, show: Option[Show])(implicit c: Connection): Int = {
      SQL("INSERT INTO session_show(session, shows, num) VALUES ({session}, {shows}, {num})")
        .on("session" -> sessionID)
        .on("shows" -> show.get.id)
        .on("num" -> i)
        .executeUpdate()
    }

    override def countByTheater(theater: String): Int = DB.withConnection { implicit c =>
      SQL(
        """
          |SELECT count(*)
          |FROM session
          |WHERE theater in (SELECT id FROM theater WHERE canonical = {theater})
          |AND date >= {now}
        """.stripMargin)
        .on("theater" -> theater)
        .on("now" -> DateUtils.toStringSQL(DateTime.now))
        .as(scalar[Int].single)
    }

    override def priceExists(id: Int, session: Option[Int]): Boolean = DB.withConnection { implicit c =>
      val count = session match {
        case Some(sessionID) =>
          SQL("SELECT count(*) FROM session_price WHERE id = {id} AND session = {session}")
            .on("id" -> id)
            .on("session" -> sessionID)
            .as(scalar[Int].single)
        case None =>
          SQL("SELECT count(*) FROM session_price WHERE id = {id}")
            .on("id" -> id)
            .as(scalar[Int].single)
      }
      count > 0
    }
  }

}
