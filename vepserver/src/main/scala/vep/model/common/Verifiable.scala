package vep.model.common

/**
 * If a class extends this trait, it means it can be checked to verify (no database constraint)
 */
trait Verifiable {
  /**
   * Checks if the class is valid.
   * @return False if the class is invalid, otherwise true
   */
  def verify: Boolean

  /**
   * Transforms this class into a result with its error(s) or success.
   * @return The result corresponding of this class
   */
  def toResult: Result
}

/**
 * This trait is a specification for Verifiable classes when they can have only one error max.
 */
trait VerifiableUnique extends Verifiable {
  /**
   * The resulting error if any
   */
  private var error: Option[Int] = None

  /**
   * Sets the error
   * @param err The error
   */
  protected def setError(err: Int) = error = Some(err)

  /**
   * Clears the error (when retesting the model for instance)
   */
  protected def clearError() = error = None

  override def toResult: Result = error match {
    case Some(err) => ResultError(err)
    case None => ResultSuccess
  }

  /**
   * Has this class an error?
   * @return True if it has an error, otherwise false
   */
  protected def hasError: Boolean = error.isDefined

  /**
   * Has this class an error?
   * @return False if it has an error, otherwise true
   */
  protected def hasNotError: Boolean = !hasError
}

/**
 * This trait is a specification for Verifiable classes when they can have several errors grouped by field.
 */
trait VerifiableMultiple extends Verifiable {
  /**
   * The map of field -> errors.
   */
  private var errors: Map[String, Seq[Int]] = Map()

  /**
   * Add an error for a field.
   * @param field The field to attach the error
   * @param err The error code
   */
  protected def addError(field: String, err: Int) = {
    errors = errors + (field -> (errors.getOrElse(field, Seq[Int]()) :+ err))
  }

  /**
   * Clears the errors (when retesting the model for instance)
   */
  protected def clearErrors() = errors = Map()

  override def toResult: Result = if (hasErrors) ResultErrors(errors) else ResultSuccess

  /**
   * Has this class an error?
   * @return True if it has an error, otherwise false
   */
  protected def hasErrors: Boolean = errors.nonEmpty

  /**
   * Has this class an error?
   * @return False if it has an error, otherwise true
   */
  protected def hasNotErrors: Boolean = !hasErrors
}