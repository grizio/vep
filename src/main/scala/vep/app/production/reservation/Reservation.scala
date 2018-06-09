package vep.app.production.reservation

import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Reservation(
  id: String,
  firstName: String,
  lastName: String,
  email: String,
  city: Option[String],
  comment: Option[String],
  newsletter: Boolean,
  seats: Seq[String],
  prices: Seq[ReservationPrice]
)

object Reservation {
  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): Reservation = {
    new Reservation(
      id = resultSet.string("id"),
      firstName = resultSet.string("first_name"),
      lastName = resultSet.string("last_name"),
      email = resultSet.string("email"),
      city = resultSet.stringOpt("city"),
      comment = resultSet.stringOpt("comment"),
      newsletter = resultSet.boolean("newsletter"),
      seats = Seq.empty,
      prices = Seq.empty
    )
  }

  implicit val reservationFormat: RootJsonFormat[Reservation] = jsonFormat9(Reservation.apply)
}

case class ReservationPrice(
  price: String,
  count: Int
)

object ReservationPrice {
  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): ReservationPrice = {
    new ReservationPrice(
      price = resultSet.string("price"),
      count = resultSet.int("seatsCount")
    )
  }

  implicit val reservationPriceFormat: RootJsonFormat[ReservationPrice] = jsonFormat2(ReservationPrice.apply)
}

case class ReservationCreation(
  firstName: String,
  lastName: String,
  email: String,
  city: Option[String],
  comment: Option[String],
  newsletter: Boolean,
  seats: Seq[String],
  prices: Seq[ReservationPrice]
)

object ReservationCreation {
  import JsonProtocol._

  implicit val reservationCreationFormat: RootJsonFormat[ReservationCreation] = jsonFormat8(ReservationCreation.apply)
}