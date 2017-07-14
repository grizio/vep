package vep.framework.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import spray.json._

trait JsonProtocol extends DefaultJsonProtocol {
  private val datetimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")

  implicit val dateTimeFormat: JsonFormat[DateTime] = new JsonFormat[DateTime] {
    override def write(obj: DateTime): JsValue = JsString(obj.toString(datetimeFormatter))

    override def read(json: JsValue): DateTime = json match {
      case JsString(jsString) =>
        try {
          DateTime.parse(jsString, datetimeFormatter)
        } catch {
          case _: Throwable => deserializationError(s"Expected ISO date, got: $jsString")
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
}

object JsonProtocol extends JsonProtocol