package vep.utils

import java.util.Date

import org.joda.time.DateTime

/**
 * This object groups all utility functions about date.
 */
object DateUtils {
  /**
   * Returns the {{{DateTime}}} corresponding to given {{{str}}} or {{{None}}} if {{{str}}} could not be parsed.
   * @param str The string to convert
   * @return The converted date
   */
  def toDateTimeOpt(str: String): Option[DateTime] = {
    try {
      Some(toDateTime(str))
    } catch {
      case e: Exception => None
    }
  }

  /**
   * Returns the {{{DateTime}}} corresponding to given {{{str}}}.
   * Unlike {{{toDateTimeOpt}}}, this function is not safe if {{{str}}} cannot be parsed.
   * @param str The string to convert
   * @return The resulting date
   */
  // Both ISO (yyyy-mm-dd\THH:mm:ss) and SQL (yyyy-mm-dd HH:mm:ss)
  def toDateTime(str: String): DateTime = DateTime.parse(str.trim.replace(" ", "T"))

  /**
   * Returns the {{{DateTime}}} corresponding to given {{{date}}}.
   * @param date The date to convert
   * @return The resulting date
   */
  def toDateTime(date: Date): DateTime = new DateTime(date)

  /**
   * Checked if the date is valid ISO-8601.
   * @param str The string to parse
   * @return {{{true}}} if the string is a valid date, {{{false}}} otherwise.
   */
  def isIsoDate(str: String): Boolean = toDateTimeOpt(str).isDefined

  /**
   * Checked if the date is invalid ISO-8601.
   * @param str The string to parse
   * @return {{{true}}} if the string is an invalid date, {{{false}}} otherwise.
   */
  def isNotIsoDate(str: String): Boolean = !isIsoDate(str)

  /**
   * Transforms the given date into a string for SQL queries.
   * @param date The date to transform
   * @return The transformed date
   */
  def toStringSQL(date: DateTime): String = date.toString("yyyy-MM-dd HH:mm:ss")

  /**
   * Transforms the given date into a string for ISO.
   * @param date The date to transform
   * @return The transformed date
   */
  def toStringISO(date: DateTime): String = {
    date.toString("yyyy-MM-dd") + "T" + date.toString("HH:mm:ss")
  }
}
