package vep.utils

import akka.util.Crypt
import com.typesafe.config.ConfigFactory
import org.apache.commons.lang3.{StringUtils => BaseStringUtils}
import org.apache.commons.validator.routines.EmailValidator

import scala.util.Random

object StringUtils {
  lazy val config = ConfigFactory.load()

  def isEmail(str: String): Boolean = EmailValidator.getInstance().isValid(str)

  def isNotEmail(str: String): Boolean = !isEmail(str)

  def isEmpty(str: String): Boolean = BaseStringUtils.isEmpty(str)

  def isNotEmpty(str: String): Boolean = !isEmpty(str)

  def isBlank(str: String): Boolean = BaseStringUtils.isBlank(str)

  def isNotBlank(str: String): Boolean = !isBlank(str)

  def equals(str1: String, str2: String): Boolean = BaseStringUtils.equals(str1, str2)

  def notEquals(str1: String, str2: String): Boolean = !equals(str1, str2)

  def containsPattern(str: String, pattern: String): Boolean = {
    str.matches("(.*)" + pattern + "(.*)")
  }

  def containsNotPattern(str: String, pattern: String): Boolean = !containsPattern(str, pattern)

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

  def isNotSecure(str: String,
                  needLetter: Boolean = true,
                  needNumber: Boolean = true,
                  needUppercaseLetter: Boolean = true,
                  needLowercaseLetter: Boolean = true,
                  needSymbol: Boolean = true,
                  minLength: Int = 8): Boolean = {
    !isSecure(str, needLetter, needNumber, needUppercaseLetter, needLowercaseLetter, needSymbol, minLength)
  }

  def generateSalt(n: Int = 25): String = Random.alphanumeric take n mkString

  def crypt(str: String, salt: String) = {
    val completeSalt = salt + (if (config.hasPath("vep.salt")) config.getString("vep.salt") else "")
    Crypt.sha1(str + completeSalt)
  }
}
