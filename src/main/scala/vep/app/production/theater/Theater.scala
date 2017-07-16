package vep.app.production.theater

import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

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

  def apply(resultSet: WrappedResultSet): Theater = {
    new Theater(
      id = resultSet.string("id"),
      name = resultSet.string("name"),
      address = resultSet.string("address"),
      content = resultSet.string("content"),
      seats = Seq.empty
    )
  }
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

  def apply(resultSet: WrappedResultSet): Seat = {
    new Seat(
      c = resultSet.string("c"),
      x = resultSet.int("x"),
      y = resultSet.int("y"),
      w = resultSet.int("w"),
      h = resultSet.int("h"),
      t = resultSet.string("t")
    )
  }
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