package vep.app.production.reservation

import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Reservation(
  id: String,
  firstName: String,
  lastName: String,
  email: String,
  city: Option[String],
  comment: Option[String],
  seats: Seq[String],
  prices: Seq[ReservationPrice]
)

object Reservation {
  import JsonProtocol._

  implicit val reservationFormat: RootJsonFormat[Reservation] = jsonFormat8(Reservation.apply)
}