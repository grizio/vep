package vep.model.session

import spray.json._
import vep.model.JsonImplicits
import vep.utils.DateUtils

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

  implicit val impSessionUpdateFormBody = new RootJsonFormat[SessionUpdateFormBody] {
    def write(session: SessionUpdateFormBody) = JsObject(
      "date" -> JsString(session.date),
      "reservationEndDate" -> JsString(session.date),
      "name" -> JsString(session.name),
      "reason" -> JsString(session.reason),
      "prices" -> JsArray(session.prices map { p => impSessionPriceForm.write(p) }: _*),
      "shows" -> JsArray(session.shows map { s => JsString(s) }: _*)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("date", "reservationEndDate", "name", "reason", "prices", "shows") match {
        case Seq(JsString(date), JsString(reservationEndDate), JsString(name), JsString(reason), JsArray(prices), JsArray(shows)) =>
          SessionUpdateFormBody(date, reservationEndDate, name, reason,
            (prices map { p => impSessionPriceForm.read(p) }).toSeq,
            shows map {
              case JsString(s) => s
              case _ => throw new DeserializationException("String expected")
            })
        case _ => throw new DeserializationException("Session expected")
      }
    }
  }

  implicit val impSessionSearch = jsonFormat(SessionSearch.apply, "t", "s", "sd", "ed", "o", "p")

  implicit val impSessionSearchShow = jsonFormat(SessionSearchShow.apply, "canonical", "title")

  implicit val impSessionSearchResult = new JsonFormat[SessionSearchResult] {
    override def write(obj: SessionSearchResult): JsValue = JsObject(
      "canonical" -> JsString(obj.canonical),
      "theater" -> JsString(obj.theater),
      "theaterName" -> JsString(obj.theaterName),
      "shows" -> JsArray(obj.shows.map(impSessionSearchShow.write): _*),
      "date" -> JsString(DateUtils.toStringISO(obj.date))
    )

    override def read(json: JsValue): SessionSearchResult = {
      json.asJsObject.getFields("canonical", "theater", "theaterName", "shows", "date") match {
        case Seq(JsString(canonical), JsString(theater), JsString(theaterName), JsArray(shows), JsString(date)) =>
          SessionSearchResult(canonical, theater, theaterName, shows.map(impSessionSearchShow.read), DateUtils.toDateTime(date))
      }
    }
  }

  implicit val impSessionSearchResponse = new RootJsonFormat[SessionSearchResponse] {
    override def write(obj: SessionSearchResponse): JsValue = JsObject(
      "sessions" -> JsArray(obj.sessions.map(impSessionSearchResult.write): _*),
      "pageMax" -> JsNumber(obj.pageMax)
    )

    override def read(json: JsValue): SessionSearchResponse = {
      json.asJsObject.getFields("sessions", "pageMax") match {
        case Seq(JsArray(sessions), JsNumber(pageMax)) =>
          SessionSearchResponse(sessions.map(impSessionSearchResult.read).toSeq, pageMax.toInt)
      }
    }
  }

  /*
   case class SessionDetail(theater: String, canonical: String, date: DateTime, name: String, reservationEndDate: DateTime,
                         prices: Seq[SessionPriceDetail], shows: Seq[String])

case class SessionPriceDetail(name: String, price: Int, cases: Option[String])
  * */

  implicit val impSessionDetail = new RootJsonFormat[SessionDetail] {
    override def write(obj: SessionDetail): JsValue = JsObject(
      "theater" -> JsString(obj.theater),
      "canonical" -> JsString(obj.canonical),
      "date" -> JsString(DateUtils.toStringISO(obj.date)),
      "name" -> JsString(obj.name),
      "reservationEndDate" -> JsString(DateUtils.toStringISO(obj.reservationEndDate)),
      "prices" -> JsArray(obj.prices.map(impSessionPriceDetail.write): _*),
      "shows" -> JsArray(obj.shows.map(s => JsString(s)): _*)
    )

    override def read(json: JsValue): SessionDetail = {
      json.asJsObject.getFields("theater", "canonical", "date", "name", "reservationEndDate", "prices", "shows") match {
        case Seq(JsString(theater), JsString(canonical), JsString(date), JsString(name), JsString(reservationEndDate), JsArray(prices), JsArray(shows)) =>
          SessionDetail(theater, canonical, DateUtils.toDateTime(date), name, DateUtils.toDateTime(reservationEndDate),
            prices.map(impSessionPriceDetail.read),
            shows map {
              case JsString(s) => s
              case _ => throw new DeserializationException("String expected")
            }
          )
      }
    }
  }

  implicit val impSessionPriceDetail: JsonFormat[SessionPriceDetail] = jsonFormat(SessionPriceDetail.apply, "name", "price", "conditions")
}