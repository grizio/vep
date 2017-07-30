package vep.app.common.types

import org.joda.time.DateTime
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Period(start: DateTime, end: DateTime)

object Period {
  import JsonProtocol._

  implicit val periodFormat: RootJsonFormat[Period] = jsonFormat2(Period.apply)
}