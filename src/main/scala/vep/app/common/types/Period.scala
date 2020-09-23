package vep.app.common.types

import java.time.LocalDateTime

import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Period(start: LocalDateTime, end: LocalDateTime)

object Period {

  import JsonProtocol._

  implicit val periodFormat: RootJsonFormat[Period] = jsonFormat2(Period.apply)
}