package vep.service.session

import java.sql.Connection

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
import vep.AnormClient
import vep.exception.FieldStructuredErrorException
import vep.model.common._
import vep.model.session.{SessionForm, SessionPriceForm}
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
      s"${canonical}_${n}"
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
  }

}
