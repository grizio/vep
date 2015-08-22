package vep.model.common

/**
 * This object defines the list of errors identified by a key.
 */
object ErrorCodes {
  val emptyEmail = 1
  val invalidEmail = 2
  val emptyFirstName = 3
  val emptyLastName = 4
  val emptyPassword = 5
  val weakPassword = 6
  val usedEmail = 7
  val differentPasswords = 8
  val unknownUser = 9
  val unauthenticated = 10
  val unauthorized = 11
  val roleUnknown = 12
  val userUnknown = 13
  val differentEmail = 14
  val invalidCanonical = 15
  val emptyField = 16
  val negativeOrNull = 17
  val usedCanonical = 18
  val unknownCanonical = 19
  val invalidJson = 20
  val bigString = 21
  val undefinedCompany = 22
  val negative = 23
  val undefinedShow = 24
  val notANumber = 25
  val unknownOrder = 26
  val undefinedTheater = 27
  val existingDateForTheater = 28
  val invalidDate = 29
  val dateTooSoon = 30
  val reservationEndDateTooLate = 31
  val emptyShowList = 32
  val undefinedSession = 33
  val lockedTheater = 34
}
