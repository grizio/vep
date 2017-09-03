package vep.framework.utils

import java.io.StringWriter

import com.github.tototoshi.csv.{CSVFormat, CSVWriter, QUOTE_MINIMAL, Quoting}

object CsvUtils {
  implicit private val csvFormat: CSVFormat = new CSVFormat {
    val delimiter: Char = ';'
    val quoteChar: Char = '"'
    val escapeChar: Char = '"'
    val lineTerminator: String = "\r\n"
    val quoting: Quoting = QUOTE_MINIMAL
    val treatEmptyLineAsNil: Boolean = false
  }

  def write(value: Seq[Seq[String]]): String = {
    val writer = new StringWriter()
    val csvWriter = CSVWriter.open(writer)
    csvWriter.writeAll(value)
    writer.toString
  }
}
