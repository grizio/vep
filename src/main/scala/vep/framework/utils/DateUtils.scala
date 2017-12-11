package vep.framework.utils

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.{Date, Locale}

import org.joda.time.DateTime

object DateUtils {
  private val locale = new Locale("fr", "FR")
  private val longFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy 'à' HH:mm").withLocale(locale)
  private val shortFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(locale)

  def longDate(date: LocalDateTime): String = {
    longFormatter.format(date)
  }

  def shortDate(date: LocalDateTime): String = {
    shortFormatter.format(date)
  }

  def fromDateTime(date: DateTime): LocalDateTime = {
    LocalDateTime.ofInstant(Instant.ofEpochMilli(date.toInstant.getMillis), ZoneId.systemDefault())
  }

  def isAfterNow(date: LocalDateTime): Boolean = {
    date.isAfter(LocalDateTime.now())
  }

  def isBeforeNow(date: LocalDateTime): Boolean = {
    date.isBefore(LocalDateTime.now())
  }

  def isBefore(date: LocalDateTime, referenceDate: LocalDateTime): Boolean = {
    date.isBefore(referenceDate)
  }

  def isAfter(date: LocalDateTime, referenceDate: LocalDateTime): Boolean = {
    date.isAfter(referenceDate)
  }
}
