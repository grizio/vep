package vep.exception

import vep.model.common.{ResultError, ResultErrors}

/**
 * This class defines an exception due to field error from database.
 *
 * @param field The field in error
 * @param code The error code
 * @param cause The error description
 */
class FieldErrorException(field: String, code: Int, cause: String)
  extends Exception("An error append for field " + field + " cause: [" + code + "]" + cause) {

  /**
   * Transforms this class into a {@code ResultErrors}.
   */
  def toResultErrors: ResultErrors = ResultErrors(Map(field -> Seq(code)))

  /**
   * Transforms this class into a {@code ResultError}.
   */
  def toResultError: ResultError = ResultError(code)
}
