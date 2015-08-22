package vep.model.session

import spray.json._
import vep.model.JsonImplicits

object SessionImplicits extends JsonImplicits {
  implicit val impSessionPriceForm: RootJsonFormat[SessionPriceForm] = jsonFormat(SessionPriceForm.apply, "name", "price", "condition")

  implicit val impSessionFormBody = new RootJsonFormat[SessionFormBody] {
    def write(session: SessionFormBody) = JsObject(
      "date" -> JsString(session.date),
      "reservationEndDate" -> JsString(session.date),
      "name" -> JsString(session.name),
      "prices" -> JsArray(session.prices map { p => impSessionPriceForm.write(p) }: _*),
      "shows" -> JsArray(session.shows map { s => JsString(s) }: _*)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("date", "reservationEndDate", "name", "prices", "shows") match {
        case Seq(JsString(date), JsString(reservationEndDate), JsString(name), JsArray(prices), JsArray(shows)) =>
          SessionFormBody(date, reservationEndDate, name,
            (prices map { p => impSessionPriceForm.read(p) }).toSeq,
            shows map {
              case JsString(s) => s
              case _ => throw new DeserializationException("String expected")
            })
        case _ => throw new DeserializationException("Session expected")
      }
    }
  }
}
