package vep.model.common

import spray.json._
import vep.model.JsonImplicits

abstract class Result {
  def isValid: Boolean
}

abstract class ResultSuccess extends Result {
  override def isValid = true
}

case object ResultSuccess extends ResultSuccess

final case class ResultError(code: Int) extends Result {
  override def isValid: Boolean = false
}

final case class ResultErrors(errors: Map[String, Seq[Int]]) extends Result {
  override def isValid: Boolean = false
}

final case class ResultSuccessEntity[A](entity: A) extends Result {
  override def isValid: Boolean = true
}

object ResultImplicits extends JsonImplicits {
  implicit val impResultSuccess: RootJsonWriter[ResultSuccess] = new RootJsonWriter[ResultSuccess] {
    override def write(obj: ResultSuccess): JsValue = JsNull
  }

  implicit val impResultError: RootJsonWriter[ResultError] = new RootJsonWriter[ResultError] {
    override def write(obj: ResultError): JsValue = JsNumber(obj.code)
  }

  implicit val impResultErrors: RootJsonWriter[ResultErrors] = new RootJsonWriter[ResultErrors] {
    override def write(obj: ResultErrors): JsValue = JsObject(obj.errors map {
      entry => entry._1 -> JsArray(entry._2.map(v => JsNumber(v)): _*)
    })
  }
}