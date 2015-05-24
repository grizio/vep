package vep.model.common

import spray.json.{DefaultJsonProtocol, JsValue, _}

abstract class Result {
  def isValid: Boolean
}

case object ResultSuccess extends Result {
  override def isValid = true
}

final case class ResultError(code: Int) extends Result {
  override def isValid: Boolean = false
}

final case class ResultErrors(errors: Map[String, Seq[Int]]) extends Result {
  override def isValid: Boolean = false
}

object ResultImplicits extends DefaultJsonProtocol {
  implicit val impResult: RootJsonFormat[Result] = new RootJsonFormat[Result] {
    override def write(obj: Result): JsValue = {
      if (obj.isValid) {
        JsNull
      } else obj match {
        case error: ResultError =>
          JsNumber(error.code)
        case error: ResultErrors =>
          JsObject(error.errors map {
            entry => entry._1 -> JsArray(entry._2.map(v => JsNumber(v)): _*)
          })
        case _ => JsNull
      }
    }

    // Should never be used
    override def read(json: JsValue): Result = null
  }
}