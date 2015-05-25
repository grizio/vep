package vep.service

import java.sql.Date
import java.text.SimpleDateFormat

import anorm.{TypeDoesNotMatch, MetaDataItem, Column}
import spray.http.DateTime

trait AnormImplicits {
  private def valueToDateTimeOption(value: Any): Option[DateTime] = {
    try {
      value match {
        case date: Date => DateTime.fromIsoDateTimeString(new SimpleDateFormat("YYYY-MM-dd").format(date))
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
        case Some(uuid) => Right(uuid)
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to UUID for column " + qualified))
      }
    }
  }
}
