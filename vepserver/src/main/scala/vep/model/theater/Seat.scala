package vep.model.theater

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

case class Seat(c: String, t: String, x: Double, y: Double, w: Double, h: Double) {
  def code = c

  def typeSeat = t

  def width = w

  def height = h
}

object Seat extends DefaultJsonProtocol {
  implicit val impSeat = jsonFormat6(Seat.apply)
}

case class SeatSeq(seats: Seq[Seat])

object SeatSeq extends DefaultJsonProtocol {
  implicit val impSeatList = jsonFormat1(SeatSeq.apply)
}