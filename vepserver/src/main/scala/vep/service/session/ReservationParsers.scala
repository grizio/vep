package vep.service.session

import anorm.SqlParser._
import anorm.~
import vep.model.session._

object ReservationParsers {
  lazy val reservation =
    int("id") ~
      int("session") ~
      str("firstName") ~
      str("lastName") ~
      str("email") ~
      get[Option[String]]("city") ~
      get[Option[String]]("comment") ~
      get[Option[Int]]("seats") ~
      get[String]("pass") map {
      case id ~ session ~ firstName ~ lastName ~ email ~ city ~ comment ~ seats ~ pass =>
        Reservation(id, session, firstName, lastName, email, city, comment, seats, pass)
    }

  lazy val reservationSeat =
    int("reservation") ~ str("seat") map {
      case reservationId ~ seat => ReservationSeat(reservationId, seat)
    }

  lazy val reservationPrice =
    int("reservation") ~ int("price") ~ int("number") map {
      case reservationId ~ price ~ number => ReservationPrice(reservationId, price, number)
    }

  lazy val reservationDetail =
    int("id") ~
      str("canonical") ~
      str("firstName") ~
      str("lastName") ~
      str("email") ~
      get[Option[String]]("city") ~
      get[Option[String]]("comment") ~
      get[Option[Int]]("seats") ~
      get[String]("pass") map {
      case id ~ session ~ firstName ~ lastName ~ email ~ city ~ comment ~ seats ~ pass =>
        ReservationDetail(id, session, firstName, lastName, email, city, comment, seats, pass, Seq(), Seq())
    }

  lazy val reservationPriceDetail =
    int("reservation_price.price") ~ int("reservation_price.number") ~ int("session_price.price") map {
      case priceId ~ number ~ value => ReservationPriceDetail(priceId, number, value)
    }
}
