package vep.framework.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
  private val locale = new Locale("fr", "FR")
  private val formatter: DateTimeFormatter = DateTimeFormat.forPattern("EEEE dd MMMM yyyy 'à' HH:mm").withLocale(locale)

  def longDate(date: DateTime): String = {
    formatter.print(date)
  }
}
