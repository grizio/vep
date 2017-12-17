package vep.app.user.adhesion

import spray.json.RootJsonFormat
import vep.app.user.UserView
import vep.framework.utils.{DateUtils, JsonProtocol}

case class AdhesionEntry(
  id: String,
  period: String,
  user: String,
  accepted: Boolean
)

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