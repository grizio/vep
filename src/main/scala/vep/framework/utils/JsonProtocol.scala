package vep.framework.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import spray.json._

trait JsonProtocol extends DefaultJsonProtocol {
  private val datetimeFormatter = DateTimeFormatter.ISO_DATE_TIME

  implicit val dateTimeFormat: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime] {
    override def write(obj: LocalDateTime): JsValue = JsString(obj.format(datetimeFormatter))

    override def read(json: JsValue): LocalDateTime = json match {
      case JsString(jsString) =>
        try {
          LocalDateTime.parse(jsString, datetimeFormatter)
        } catch {
          case t: Throwable =>
            deserializationError(s"Expected ISO date, got: $jsString")
        }
      case x =>
        deserializationError(s"String expected, got: $x")
    }
  }

  def enumFormat[A <: Enumeration, B](enum: A, enumWithName: String => B): RootJsonFormat[B] = new RootJsonFormat[B] {
    override def write(obj: B): JsValue = JsString(obj.toString)

    override def read(json: JsValue): B = json match {
      case JsString(string) =>
        try {
          enumWithName(string)
        } catch {
          case _: Throwable => deserializationError(s"One of ${enum.values.mkString("('", "', '", "')")} expected, got: $string")
        }
      case x => deserializationError(s"String expected, got: $x")
    }
  }

  def seqJsonFormat[A](implicit internal: JsonFormat[A]): RootJsonFormat[Seq[A]] = viaSeq[scala.collection.immutable.Seq[A], A](seq => Seq(seq: _*))(internal)
}

object JsonProtocol extends JsonProtocol