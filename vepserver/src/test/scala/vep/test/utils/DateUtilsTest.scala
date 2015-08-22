package vep.test.utils

import org.joda.time.{DateTime, Duration}
import vep.utils.DateUtils

object DateUtilsTest {
  def nowPlusOneMinute = DateTime.now().withDurationAdded(Duration.standardMinutes(1), 1).withMillisOfSecond(0)

  def nowPlusOneMinuteISO = DateUtils.toStringISO(nowPlusOneMinute)

  def nowPlusOneDay = DateTime.now().withDurationAdded(Duration.standardDays(1), 1).withMillisOfSecond(0)

  def nowPlusOneDayISO = DateUtils.toStringISO(nowPlusOneDay)

  def nowPlusTwoDays = DateTime.now().withDurationAdded(Duration.standardDays(2), 1).withMillisOfSecond(0)

  def nowPlusTwoDaysISO = DateUtils.toStringISO(nowPlusTwoDays)

  def nowMinusOneMinute = DateTime.now().withDurationAdded(Duration.standardMinutes(1), -1).withMillisOfSecond(0)

  def nowMinusOneMinuteISO = DateUtils.toStringISO(nowMinusOneMinute)

  def nowMinusOneDay = DateTime.now().withDurationAdded(Duration.standardDays(1), -1).withMillisOfSecond(0)

  def nowMinusOneDayISO = DateUtils.toStringISO(nowMinusOneDay)

  def nowMinusTwoDays = DateTime.now().withDurationAdded(Duration.standardDays(2), -1).withMillisOfSecond(0)

  def nowMinusTwoDaysISO = DateUtils.toStringISO(nowMinusTwoDays)
}
