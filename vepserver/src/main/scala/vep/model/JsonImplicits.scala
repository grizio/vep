package vep.model

import spray.http.DateTime
import spray.json.{JsString, DefaultJsonProtocol, JsValue, RootJsonFormat}

trait JsonImplicits extends DefaultJsonProtocol {
  implicit val impDateTime: RootJsonFormat[DateTime] = new RootJsonFormat[DateTime] {
    override def write(obj: DateTime): JsValue = JsString(obj.toIsoDateString)

    override def read(json: JsValue): DateTime = DateTime.fromIsoDateTimeString(json.toString()).get
  }

  def seqToJson(col: Seq[String]): String = {
    col map { s => s.replace("\"", "\\\"") } mkString ("[\"", "\",\"", "\"]")
  }
}

object JsonImplicits extends JsonImplicits