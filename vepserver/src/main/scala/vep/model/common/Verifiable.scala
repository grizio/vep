package vep.model.common

class IncompatibleArgumentException(e: String) extends Exception(e)

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

  // TODO: make final when children use doVerify
  override def verify: Boolean = {
    doVerify()
    hasNotErrors
  }

  /**
   * This method is defined to permit the developer to define its verification method.
   * It will be called on {{{verify}}} call (if not overridden)
   * before returning the success flag with {{{hasNotErrors}}}.
   */
  // TODO: remove implementation when all class use this method
  protected def doVerify(): Unit = {}
}

/**
 * This trait is a specification for Verifiable classes when they can have several errors grouped by field.
 */
trait VerifiableMultipleStructured extends Verifiable {

  /**
   * The map of field -> errors.
   */
  private var _errors: ErrorItemTree = ErrorItemTree(Map[String, ErrorItem]())

  def errors = _errors

  /**
   * Add an error for a field.
   * @param field The field to attach the error
   * @param err The error code
   */
  protected def addError(field: String, err: Int): Unit = {
    val element = _errors.e.getOrElse(field, ErrorItemFinal(Seq())) match {
      case ErrorItemFinal(e) => ErrorItemFinal(e :+ err)
      case ErrorItemTree(e) => throw new IncompatibleArgumentException("You are trying to add an integer error into a tree error")
      case ErrorItemSeq(e) => throw new IncompatibleArgumentException("You are trying to add an integer error into a seq error")
    }
    _errors = ErrorItemTree(_errors.e + (field -> element))
  }

  protected def addSeqErrors(field: String, errors: Map[Int, ErrorItem]): Unit = {
    val element = _errors.e.getOrElse(field, ErrorItemSeq(Map[Int, ErrorItem]())) match {
      case ErrorItemFinal(e) => throw new IncompatibleArgumentException("You are trying to add a seq error into a integer error")
      case ErrorItemTree(e) => throw new IncompatibleArgumentException("You are trying to add a seq error into a tree error")
      case ErrorItemSeq(e) => ErrorItemSeq(e ++ errors)
    }
    _errors = ErrorItemTree(_errors.e + (field -> element))
  }

  protected def addMapErrors(field: String, errors: Map[String, ErrorItem]): Unit = {
    val element = _errors.e.getOrElse(field, ErrorItemSeq(Map[Int, ErrorItem]())) match {
      case ErrorItemFinal(e) => throw new IncompatibleArgumentException("You are trying to add a tree error into a integer error")
      case ErrorItemTree(e) => ErrorItemTree(e ++ errors)
      case ErrorItemSeq(e) => throw new IncompatibleArgumentException("You are trying to add a tree error into a seq error")
    }
    _errors = ErrorItemTree(_errors.e + (field -> element))
  }

  protected def addErrorItem(field: String, error: ErrorItem): Unit = {
    error match {
      case ErrorItemFinal(err) => err.foreach(addError(field, _))
      case ErrorItemTree(err) => addMapErrors(field, err)
      case ErrorItemSeq(err) => addSeqErrors(field, err)
    }
  }

  /**
   * Clears the errors (when retesting the model for instance)
   */
  protected def clearErrors() = _errors = ErrorItemTree(Map[String, ErrorItem]())

  override def toResult: Result = if (hasErrors) ResultStructuredErrors(_errors) else ResultSuccess

  /**
   * Has this class an error?
   * @return True if it has an error, otherwise false
   */
  protected def hasErrors: Boolean = !hasNotErrors

  /**
   * Has this class an error?
   * @return False if it has an error, otherwise true
   */
  protected def hasNotErrors: Boolean = _errors.isEmpty

  // TODO: make final when children use doVerify
  override def verify: Boolean = {
    doVerify()
    hasNotErrors
  }

  /**
   * This method is defined to permit the developer to define its verification method.
   * It will be called on {{{verify}}} call (if not overridden)
   * before returning the success flag with {{{hasNotErrors}}}.
   */
  // TODO: remove implementation when all class use this method
  protected def doVerify(): Unit = {}
}