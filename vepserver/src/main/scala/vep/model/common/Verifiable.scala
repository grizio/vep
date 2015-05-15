package vep.model.common

trait Verifiable {
  def verify: Boolean

  def toResult: Result
}

trait VerifiableUnique extends Verifiable {
  private var error: Option[Int] = None

  protected def setError(err: Int) = error = Some(err)

  protected def clearError() = error = None

  override def toResult: Result = error match {
    case Some(err) => ResultError(err)
    case None => ResultSuccess
  }

  protected def hasError: Boolean = error.isDefined
  protected def hasNotError: Boolean = !hasError
}

trait VerifiableMultiple extends Verifiable {
  private var errors: Map[String, Seq[Int]] = Map()

  protected def addError(field: String, err: Int) = {
    errors = errors + (field -> (errors.getOrElse(field, Seq[Int]()) :+ err))
  }

  protected def clearErrors() = errors = Map()

  override def toResult: Result = if (hasErrors) ResultErrors(errors) else ResultSuccess

  protected def hasErrors: Boolean = errors.nonEmpty
  protected def hasNotErrors: Boolean = !hasErrors
}