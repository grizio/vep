package vep.exception

import vep.model.common.{ResultError, ResultErrors}

class FieldErrorException(field: String, code: Int, cause: String)
  extends Exception("An error append for field " + field + " cause: [" + code + "]" + cause) {

  def toResultErrors: ResultErrors = ResultErrors(Map(field -> Seq(code)))

  def toResultError: ResultError = ResultError(code)
}
