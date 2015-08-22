package vep.exception

import vep.model.common.{ErrorItem, ResultError, ResultErrors, ResultStructuredErrors}

/**
 * This class defines an exception due to field error from database.
 *
 * @param field The field in error
 * @param code The error code
 * @param cause The error description
 */
class FieldErrorException(field: String, code: Int, cause: String)
  extends Exception("An error append for field " + field + ". Cause: [" + code + "] " + cause) {

  /**
   * Transforms this class into a {{{ResultErrors}}}.
   */
  def toResultErrors: ResultErrors = ResultErrors(Map(field -> Seq(code)))

  /**
   * Transforms this class into a {{{ResultError}}}.
   */
  def toResultError: ResultError = ResultError(code)
}

class FieldStructuredErrorException(val cause: String, val error: ErrorItem)
  extends Exception("An error append for a field. Cause: " + cause) {
  def this(error: ErrorItem, cause: String) = this(cause, error)

  /**
   * Transforms this class into a {{{ResultStructuredErrors}}}.
   */
  def toResult: ResultStructuredErrors = ResultStructuredErrors(error)
}