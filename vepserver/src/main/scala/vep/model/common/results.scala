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
 * @param errors The list of errors, grouped by field
 */
final case class ResultErrors(errors: Map[String, Seq[Int]]) extends Result {
  override def isValid: Boolean = false
}

/**
 * This class defines an error with at least one error, structured by {{{ErrorItem}}} type.
 * @param errors The list of errors
 */
final case class ResultStructuredErrors(errors: ErrorItem) extends Result {
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

  implicit val impErrorItem: RootJsonWriter[ErrorItem] = new RootJsonWriter[ErrorItem] {
    override def write(obj: ErrorItem): JsValue = obj match {
      case e: ErrorItemFinal => impErrorItemFinal.write(e)
      case e: ErrorItemTree => impErrorItemTree.write(e)
      case e: ErrorItemSeq => impErrorItemSeq.write(e)
    }
  }

  implicit val impErrorItemFinal: RootJsonWriter[ErrorItemFinal] = new RootJsonWriter[ErrorItemFinal] {
    override def write(obj: ErrorItemFinal): JsValue = JsArray(
      obj.e map { code => JsNumber(code) }: _*
    )
  }

  implicit val impErrorItemTree: RootJsonWriter[ErrorItemTree] = new RootJsonWriter[ErrorItemTree] {
    override def write(obj: ErrorItemTree): JsValue = JsObject(
      obj.e map { entry => entry._1 -> impErrorItem.write(entry._2) } toSeq: _*
    )
  }

  implicit val impErrorItemSeq: RootJsonWriter[ErrorItemSeq] = new RootJsonWriter[ErrorItemSeq] {
    override def write(obj: ErrorItemSeq): JsValue = JsObject(
      obj.e map { entry => entry._1.toString -> impErrorItem.write(entry._2) } toSeq: _*
    )
  }

  implicit val impResultStructuredErrors: RootJsonWriter[ResultStructuredErrors] = new RootJsonWriter[ResultStructuredErrors] {
    override def write(obj: ResultStructuredErrors): JsValue = impErrorItem.write(obj.errors)
  }
}