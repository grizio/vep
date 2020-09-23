package vep.framework.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
}
