package vep.service

import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat

import anorm.{TypeDoesNotMatch, MetaDataItem, Column}
import spray.http.DateTime

/**
 * Helper class to convert data for Anorm.
 */
trait AnormImplicits {
  private def valueToDateTimeOption(value: Any): Option[DateTime] = {
    try {
      value match {
        case date: Date => DateTime.fromIsoDateTimeString(new SimpleDateFormat("YYYY-MM-dd").format(date))
        case time: Timestamp => DateTime.fromIsoDateTimeString(new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss").format(time))
        case _ => None
      }
    }
    catch {
      case e: Exception => None
    }
  }

  implicit def rowToDateTime: Column[DateTime] = {
    Column.nonNull1[DateTime] { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      valueToDateTimeOption(value) match {
        case Some(dateTime) => Right(dateTime)
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to DateTime for column " + qualified))
      }
    }
  }
}
