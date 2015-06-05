package vep.model.common

import spray.json._
import vep.model.JsonImplicits

/**
 * This class defines a result returned by all controller.
 */
abstract class Result {
  def isValid: Boolean
}

/**
 * This class defines a success without any returned entity.
 */
abstract class ResultSuccess extends Result {
  override def isValid = true
}

/**
 * @see ResultSuccess
 */
case object ResultSuccess extends ResultSuccess

/**
 * This class defines an error with an error code.
 * @param code The error code
 */
final case class ResultError(code: Int) extends Result {
  override def isValid: Boolean = false
}

/**
 * This class defines an error with at least one error, errors grouped by field.
 * @param errors This list of errors, grouped by field
 */
final case class ResultErrors(errors: Map[String, Seq[Int]]) extends Result {
  override def isValid: Boolean = false
}

/**
 * This class defines a success with an entity as result.
 * @param entity The resulting entity
 * @tparam A The type of the resulting entity
 */
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