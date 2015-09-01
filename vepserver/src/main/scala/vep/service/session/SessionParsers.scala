package vep.service.session

import java.util.Date

import anorm.SqlParser._
import anorm.~
import org.joda.time.DateTime
import vep.model.session._
import vep.utils.DateUtils

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

  lazy val sessionDetailParser =
    int("session.id") ~
      str("theater.canonical") ~
      str("session.canonical") ~
      date("session.date") ~
      str("session.name") ~
      date("session.reservationEndDate") map {
      case id ~ theater ~ canonical ~ date ~ name ~ reservationEndDate =>
        SessionDetailParsed(id, theater, canonical, DateUtils.toDateTime(date), name, DateUtils.toDateTime(reservationEndDate))
    }

  lazy val sessionPriceDetailParser =
    str("name") ~
      int("price") ~
      get[Option[String]]("cases") map {
      case name ~ price ~ cases =>
        SessionPriceDetail(name, price, cases)
    }

  lazy val sessionSearchParser =
    str("theater.canonical") ~
      str("theater.name") ~
      int("session.id") ~
      str("session.canonical") ~
      get[Date]("session.date") map {
      case theater ~ theaterName ~ id ~ canonical ~ date =>
        SessionSearchParsed(theater, theaterName, id, canonical, DateUtils.toDateTime(date))
    }

  lazy val sessionSearchShowParser =
    str("canonical") ~ str("title") map {
      case canonical ~ title => SessionSearchShow(canonical, title)
    }
}
