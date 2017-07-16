package vep.app.production.theater

import spray.json.RootJsonFormat
import vep.app.user.registration.UserRegistration
import vep.framework.utils.JsonProtocol
import vep.framework.utils.JsonProtocol.jsonFormat2

case class Theater(
  id: String,
  name: String,
  address: String,
  content: String,
  seats: Seq[Seat]
)

object Theater {
  import JsonProtocol._

  implicit val theaterFormat: RootJsonFormat[Theater] = jsonFormat5(Theater.apply)
}

case class Seat(
  c: String,
  x: Int,
  y: Int,
  w: Int,
  h: Int,
  t: String
)

object Seat {
  import JsonProtocol._

  implicit val seatFormat: RootJsonFormat[Seat] = jsonFormat6(Seat.apply)
  implicit val seqSeatFormat: RootJsonFormat[Seq[Seat]] = seqFormat[Seat]
}

case class TheaterCreation(
  name: String,
  address: String,
  content: String,
  seats: Seq[Seat]
)

object TheaterCreation {
  import JsonProtocol._

  implicit val theaterCreationFormat: RootJsonFormat[TheaterCreation] = jsonFormat4(TheaterCreation.apply)
}