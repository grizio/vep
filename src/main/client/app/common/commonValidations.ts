import {Invalid, Valid, Validation} from "../framework/utils/Validation"
import messages from "../framework/messages"
import {
  hasLowercase, hasNumber, hasUppercase, isNotNumber
} from "../framework/utils/strings"

const patternValidatingEmail: RegExp = new RegExp("^([a-zA-Z0-9.!#$%&’'*+/=?^_`{|}~-]+)@([a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*)$")
const scoreMultiplicationByCharacter = 1
const scoreAdditionWhenUppercaseLetter = 10
const scoreAdditionWhenLowercaseLetter = 10
const scoreAdditionWhenNumber = 10
const scoreAdditionWhenSpecialCharacter = 20
const minimumScoreRequired = 400

export function validateEmail(email: string): Validation<string> {
  return Valid(email)
    .filter(value => patternValidatingEmail.test(value), messages.common.invalidEmail)
}

export function validatePassword(password: string): Validation<string> {
  const score = computeSecurityScore(password)
  if (score >= minimumScoreRequired) {
    return Valid(password)
  } else {
    return Invalid([messages.common.invalidPasswordScore(score, minimumScoreRequired)])
  }
}

function computeSecurityScore(value: string): number {
  let total = 0
  if (hasUppercase(value)) total += scoreAdditionWhenUppercaseLetter
  if (hasLowercase(value)) total += scoreAdditionWhenLowercaseLetter
  if (hasNumber(value)) total += scoreAdditionWhenNumber
  if (containsSpecialCharacter(value)) total += scoreAdditionWhenSpecialCharacter
  total *= value.length * scoreMultiplicationByCharacter
  return total
}

function containsSpecialCharacter(value: String) {
  if (!value) return false

  for (let i = 0; i < value.length; i++) {
    const char = value.charAt(i)
    if (isNotNumber(char) && char.toLowerCase() === char.toUpperCase()) {
      return true
    }
  }
  return false
}

export function validateNotNull<A>(value: A): Validation<A> {
  return Valid(value)
    .filter(value => value !== null && value !== undefined, messages.common.isNull)
}

export function validateNonEmpty(value: string): Validation<string> {
  return Valid(value)
    .filter(value => value && value !== "", messages.common.emptyString)
}

export function validateNonBlank(value: string): Validation<string> {
  return validateNonEmpty(value)
    .filter(value => value.trim() !== "", messages.common.emptyString)
}

export function validateNonEmptyArray<A>(value: Array<A>): Validation<Array<A>> {
  return Valid(value)
    .filter(value => value && value.length > 0, messages.common.emptyArray)
}

export function validatePositiveNumber(value: number): Validation<number> {
  return Valid(value)
    .filter(value => value !== null && value !== undefined && value > 0, messages.common.notPositive)
}

export function validateFuture(value: Date): Validation<Date> {
  return Valid(value)
    .filter(value => !value || value.getTime() > (new Date()).getTime(), messages.common.notFuture)
}

export function validatePassed(value: Date): Validation<Date> {
  return Valid(value)
    .filter(value => !value || value.getTime() < (new Date()).getTime(), messages.common.notFuture)
}

export function validateCanonical(value: string): Validation<string> {
  return Valid(value)
    .filter(value => /^[a-zA-Z0-9-]+$/.test(value), messages.common.invalidCanonical)
}