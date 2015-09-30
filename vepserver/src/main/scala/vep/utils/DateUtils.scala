package vep.utils

import java.util.Date

import org.joda.time.DateTime

/**
 * This object groups all utility functions about date.
 */
object DateUtils {
  val days = Map(
    1 -> "lundi",
    2 -> "mardi",
    3 -> "mercredi",
    4 -> "jeudi",
    5 -> "vendredi",
    6 -> "samedi",
    7 -> "dimanche"
  )
  val months = Map(
    1 -> "janvier",
    2 -> "février",
    3 -> "mars",
    4 -> "avril",
    5 -> "mai",
    6 -> "juin",
    7 -> "juillet",
    8 -> "août",
    9 -> "septembre",
    10 -> "octobre",
    11 -> "novembre",
    12 -> "décembre"
  )

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

  /**
   * Transforms the given date into a string readable by a human (short version).
   * @param date The date to transform
   * @param withTime If true, includes time
   * @return The transformed date
   */
  def toStringDisplay(date: DateTime, withTime: Boolean = true): String = {
    if (withTime) {
      date.toString("dd/MM/yyyy") + " à " + date.toString("HH:mm")
    } else {
      date.toString("dd/MM/yyyy")
    }
  }

  /**
   * Transforms the given date into a string readable by a human (long version).
   * @param date The date to transform
   * @param withTime If true, includes time
   * @return The transformed date
   */
  def toStringDisplayLong(date: DateTime, withTime: Boolean = true): String = {
    val day = days(date.getDayOfWeek)
    val month = months(date.getMonthOfYear)
    val dateStr = s"$day ${date.toString("dd")} $month ${date.toString("yyyy")}"
    if (withTime) {
      dateStr + " à " + date.toString("HH:mm")
    } else {
      dateStr
    }
  }
}
