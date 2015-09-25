package vep.model.session

import spray.json._
import vep.model.JsonImplicits

object ReservationImplicits extends JsonImplicits {
  implicit val impReservationPriceForm = jsonFormat2(ReservationPriceForm.apply)

  implicit val impReservationFormBody = jsonFormat8(ReservationFormBody.apply)

  implicit val impReservationFormResult = jsonFormat2(ReservationFormResult)

  /*implicit val impReservationFormBody = new RootJsonReader[ReservationFormBody] {
    override def read(json: JsValue): ReservationFormBody = {
      json.asJsObject.getFields("firstName", "lastName", "email", "city", "comment", "seats", "seatList", "prices") match {
      case Seq(JsString(firstName), JsString(lastName), JsString(email), JsString(city), JsString(comment), JsNumber(seats), JsArray(seatList), JsArray(prices)) =>
        ReservationFormBody(firstName, lastName, email, city, comment, seats,
          (prices map { p => impReservationPriceForm.read(p) }).toSeq,
          seatList map {
            case JsString(s) => s
            case _ => throw new DeserializationException("String expected")
          })
      case _ => throw new DeserializationException("Session expected")
    }
  }
  }

  implicit val impReservationPriceForm = new RootJsonReader[ReservationPriceForm] {
    override def read(json: JsValue): ReservationPriceForm = ???
  }

  implicit val impReservationFormResult = new RootJsonWriter[ReservationFormResult] {
    override def write(obj: ReservationFormResult): JsValue = ???
  }*/
}
