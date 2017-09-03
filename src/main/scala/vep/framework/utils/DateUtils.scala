package vep.framework.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
  private val locale = new Locale("fr", "FR")
  private val longFormatter: DateTimeFormatter = DateTimeFormat.forPattern("EEEE dd MMMM yyyy 'à' HH:mm").withLocale(locale)
  private val shortFormatter: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(locale)

  def longDate(date: DateTime): String = {
    longFormatter.print(date)
  }

  def shortDate(date: DateTime): String = {
    shortFormatter.print(date)
  }
}
