package vep.app.user.adhesion

import org.joda.time.DateTime
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

case class Adhesion(
  id: String,
  user: String,
  accepted: Boolean,
  members: Seq[AdhesionMember]
)

object Adhesion {
  import JsonProtocol._

  implicit val adhesionFormat: RootJsonFormat[Adhesion] = jsonFormat4(Adhesion.apply)
}

case class AdhesionMember(
  firstName: String,
  lastName: String,
  birthday: DateTime,
  activity: String
)

object AdhesionMember {
  import JsonProtocol._

  implicit val adhesionMemberFormat: RootJsonFormat[AdhesionMember] = jsonFormat4(AdhesionMember.apply)
}

case class RequestAdhesion(
  period: String,
  members: Seq[AdhesionMember]
)

object RequestAdhesion {
  import JsonProtocol._

  implicit val requestAdhesionFormat: RootJsonFormat[RequestAdhesion] = jsonFormat2(RequestAdhesion.apply)
}