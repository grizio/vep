package vep.app.user.adhesion

import spray.json.{JsonFormat, RootJsonFormat}
import vep.app.common.types.Period
import vep.framework.utils.JsonProtocol

case class PeriodAdhesion(
  id: String,
  period: Period,
  registrationPeriod: Period,
  activities: Seq[String]
)

object PeriodAdhesion {
  import JsonProtocol._

  implicit val periodAdhesionFormat: RootJsonFormat[PeriodAdhesion] = jsonFormat4(PeriodAdhesion.apply)
  val activitiesFormat: JsonFormat[Seq[String]] = seqFormat[String]
}

case class PeriodAdhesionCreation(
  period: Period,
  registrationPeriod: Period,
  activities: Seq[String]
)

object PeriodAdhesionCreation {
  import JsonProtocol._

  implicit val periodAdhesionCreationFormat: RootJsonFormat[PeriodAdhesionCreation] = jsonFormat3(PeriodAdhesionCreation.apply)
}