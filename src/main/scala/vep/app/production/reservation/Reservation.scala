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
  seats: Seq[String]
)

object Reservation {
  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): Reservation = {
    new Reservation(
      id = resultSet.string("id"),
      firstName = resultSet.string("firstName"),
      lastName = resultSet.string("lastName"),
      email = resultSet.string("email"),
      city = resultSet.stringOpt("city"),
      comment = resultSet.stringOpt("comment"),
      seats = Seq.empty
    )
  }

  implicit val reservationFormat: RootJsonFormat[Reservation] = jsonFormat7(Reservation.apply)
}

case class ReservationCreation(
  firstName: String,
  lastName: String,
  email: String,
  city: Option[String],
  comment: Option[String],
  seats: Seq[String]
)

object ReservationCreation {
  import JsonProtocol._

  implicit val reservationCreationFormat: RootJsonFormat[ReservationCreation] = jsonFormat6(ReservationCreation.apply)
}