package vep.app.user.adhesion

import scalikejdbc.WrappedResultSet
import spray.json.{JsonFormat, JsonParser, RootJsonFormat}
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

  def apply(resultSet: WrappedResultSet): PeriodAdhesion = {
    new PeriodAdhesion(
      id = resultSet.string("id"),
      period = Period(
        start = resultSet.jodaDateTime("startPeriod"),
        end = resultSet.jodaDateTime("endPeriod")
      ),
      registrationPeriod = Period(
        start = resultSet.jodaDateTime("startRegistration"),
        end = resultSet.jodaDateTime("endRegistration")
      ),
      activities = activitiesFormat.read(JsonParser(resultSet.string("activities")))
    )
  }

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