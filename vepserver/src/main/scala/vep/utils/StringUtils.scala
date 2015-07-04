package vep.utils

import akka.util.Crypt
import com.typesafe.config.ConfigFactory
import org.apache.commons.lang3.{StringUtils => BaseStringUtils}
import org.apache.commons.validator.routines.EmailValidator

import scala.util.Random
import scala.util.parsing.json.JSON

/**
 * This class grups all String utilities.
 * It can delegates to external utilities (apache common lang) or defined its own one.
 * @see EmailValidator
 * @see BaseStringUtils
 * @see Crypt
 */
object StringUtils {
  lazy val config = ConfigFactory.load()

  /**
   * Is the given string an email?
   */
  def isEmail(str: String): Boolean = EmailValidator.getInstance().isValid(str)

  /**
   * Is the given string not an email?
   */
  def isNotEmail(str: String): Boolean = !isEmail(str)

  /**
   * Is the given string null or empty?
   */
  def isEmpty(str: String): Boolean = BaseStringUtils.isEmpty(str)

  /**
   * Is the given string not null nor empty?
   */
  def isNotEmpty(str: String): Boolean = !isEmpty(str)

  /**
   * Is the given string null, empty or contains only blank characters?
   */
  def isBlank(str: String): Boolean = BaseStringUtils.isBlank(str)

  /**
   * Is the given string not null, empty nor contains only blank characters?
   */
  def isNotBlank(str: String): Boolean = !isBlank(str)

  /**
   * Are the two strings equal?
   */
  def equals(str1: String, str2: String): Boolean = BaseStringUtils.equals(str1, str2)

  /**
   * Are the two strings equal?
   */
  def notEquals(str1: String, str2: String): Boolean = !equals(str1, str2)

  /**
   * Does the given string contain the given pattern?
   */
  def containsPattern(str: String, pattern: String): Boolean = {
    str.matches("(.*)" + pattern + "(.*)")
  }

  /**
   * Does the given string contain not the given pattern?
   */
  def containsNotPattern(str: String, pattern: String): Boolean = !containsPattern(str, pattern)

  /**
   * Is the string secured in terms of given configuration (default: all)
   */
  def isSecure(str: String,
               needLetter: Boolean = true,
               needNumber: Boolean = true,
               needUppercaseLetter: Boolean = true,
               needLowercaseLetter: Boolean = true,
               needSymbol: Boolean = true,
               minLength: Int = 8): Boolean = {
    if (str.length < minLength) false
    else if (needLetter && containsNotPattern(str, "[A-Za-z]")) false
    else if (needNumber && containsNotPattern(str, "[0-9]")) false
    else if (needUppercaseLetter && containsNotPattern(str, "[A-Z]")) false
    else if (needLowercaseLetter && containsNotPattern(str, "[a-z]")) false
    else if (needSymbol && containsNotPattern(str, "[^A-Za-z0-9]")) false
    else true
  }

  /**
   * Is the string not secured in terms of given configuration (default: all)
   */
  def isNotSecure(str: String,
                  needLetter: Boolean = true,
                  needNumber: Boolean = true,
                  needUppercaseLetter: Boolean = true,
                  needLowercaseLetter: Boolean = true,
                  needSymbol: Boolean = true,
                  minLength: Int = 8): Boolean = {
    !isSecure(str, needLetter, needNumber, needUppercaseLetter, needLowercaseLetter, needSymbol, minLength)
  }

  /**
   * Generates a salt in given length.
   * @param n The length of the salt
   * @return The generated salt
   */
  def generateSalt(n: Int = 25): String = Random.alphanumeric take n mkString

  /**
   * Crypts the given string with given salt.
   * @param str The string to crypt
   * @param salt The salt to use
   * @return The Crypted string
   */
  def crypt(str: String, salt: String): String = {
    val completeSalt = salt + (if (config.hasPath("vep.salt")) config.getString("vep.salt") else "")
    Crypt.sha1(str + completeSalt)
  }

  /**
   * Checks is the given string is a canonical (contains only letters, numbers, _, + or -, does not start or end by a symbol).
   * @param str The string to check
   * @return True if the string is a canonical, otherwise false.
   */
  def isCanonical(str: String): Boolean = {
    val symbols = List('+', '-', '_')
    !str.isEmpty && str.matches("^[a-z0-9_+-]+$") && !symbols.contains(str.head) && !symbols.contains(str.last)
  }

  /**
   * Checks is the given string is not a canonical.
   * @param str The string to check
   * @return True if the string is not a canonical, otherwise false.
   */
  def isNotCanonical(str: String): Boolean = !isCanonical(str)

  /**
   * Checks if the given string is in JSON format.
   * @param str The string to check
   * @return True if the string is in JSON format, otherwise false.
   */
  def isJson(str: String): Boolean = JSON.parseRaw(str).isDefined

  /**
   * Checks if the given string is not in JSON format.
   * @param str The string to check
   * @return True if the string is not in JSON format, otherwise false.
   */
  def isNotJson(str: String): Boolean = !isJson(str)
}
