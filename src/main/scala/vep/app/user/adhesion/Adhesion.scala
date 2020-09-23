package vep.app.user.adhesion

import java.time.LocalDateTime

import scalikejdbc.WrappedResultSet
import spray.json.{JsonFormat, JsonParser, RootJsonFormat}
import vep.app.common.types.Period
import vep.app.user.UserView
import vep.framework.utils.{DateUtils, JsonProtocol}

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
        start = resultSet.localDateTime("startPeriod"),
        end = resultSet.localDateTime("endPeriod")
      ),
      registrationPeriod = Period(
        start = resultSet.localDateTime("startRegistration"),
        end = resultSet.localDateTime("endRegistration")
      ),
      activities = activitiesFormat.read(JsonParser(resultSet.string("activities")))
    )
  }

  implicit val periodAdhesionFormat: RootJsonFormat[PeriodAdhesion] = jsonFormat4(PeriodAdhesion.apply)
  val activitiesFormat: JsonFormat[Seq[String]] = seqJsonFormat[String]
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
  birthday: LocalDateTime,
  activity: String
)

object AdhesionMember {

  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): AdhesionMember = {
    new AdhesionMember(
      firstName = resultSet.string("first_name"),
      lastName = resultSet.string("last_name"),
      birthday = resultSet.localDateTime("birthday"),
      activity = resultSet.string("activity")
    )
  }

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

case class AdhesionEntry(
  id: String,
  period: String,
  user: String,
  accepted: Boolean
)

object AdhesionEntry {
  def apply(resultSet: WrappedResultSet): AdhesionEntry = {
    new AdhesionEntry(
      id = resultSet.string("id"),
      user = resultSet.string("user_id"),
      period = resultSet.string("period"),
      accepted = resultSet.boolean("accepted")
    )
  }
}

case class AdhesionView(
  id: String,
  period: PeriodAdhesion,
  user: UserView,
  accepted: Boolean,
  members: Seq[AdhesionMember]
) {
  def toCsv: Seq[Seq[String]] = {
    members.map { member =>
      Seq(
        DateUtils.shortDate(period.period.start), DateUtils.shortDate(period.period.end),
        user.email, user.firstName, user.lastName,
        member.firstName, member.lastName, DateUtils.shortDate(member.birthday), member.activity,
        if (accepted) "oui" else "non"
      )
    }
  }
}

object AdhesionView {

  import JsonProtocol._

  implicit val adhesionViewFormat: RootJsonFormat[AdhesionView] = jsonFormat5(AdhesionView.apply)

  lazy val csvHeader = Seq(
    "Début", "Fin",
    "Adresse e-mail", "Prénom", "Nom",
    "Prénom participant·e", "Nom participant·e", "Date de naissance participant·e", "Activité participant·e",
    "Accepté"
  )

  def toCsv(adhesionViews: Seq[AdhesionView]): Seq[Seq[String]] = {
    csvHeader +: adhesionViews.flatMap(_.toCsv)
  }
}