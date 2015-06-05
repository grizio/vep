package vep.model

import spray.http.DateTime
import spray.json._
import vep.model.user.RolesSeq

trait JsonImplicits extends DefaultJsonProtocol {
  implicit val impDateTime: RootJsonFormat[DateTime] = new RootJsonFormat[DateTime] {
    override def write(obj: DateTime): JsValue = JsString(obj.toIsoDateString)

    override def read(json: JsValue): DateTime = DateTime.fromIsoDateTimeString(json.toString()).get
  }
}

object JsonImplicits extends JsonImplicits