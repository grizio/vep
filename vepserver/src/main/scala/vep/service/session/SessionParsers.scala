package vep.service.session

import anorm.SqlParser._
import anorm.~
import org.joda.time.DateTime
import vep.model.session._

object SessionParsers {
  lazy val session =
    int("id") ~
      int("theater") ~
      str("canonical") ~
      get[DateTime]("date") ~
      str("name") ~
      get[DateTime]("reservationEndDate") map {
      case id ~ theater ~ canonical ~ date ~ name ~ reservationEndDate =>
        Session(id, theater, canonical, date, name, reservationEndDate)
    }

  lazy val sessionPrice =
    int("id") ~
      int("session") ~
      str("name") ~
      int("price") ~
      get[Option[String]]("cases") map {
      case id ~ sessionId ~ name ~ price ~ cases =>
        SessionPrice(id, sessionId, name, price, cases)
    }


  lazy val sessionShow =
    int("session") ~
      int("shows") ~
      int("num") map {
      case sessionId ~ show ~ num =>
        SessionShow(sessionId, show, num)
    }
}
