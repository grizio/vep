package vep.app.production.company.show.play

import java.time.LocalDateTime

import spray.json.RootJsonFormat
import vep.app.production.company.Company
import vep.app.production.company.show.Show
import vep.app.production.theater.Theater
import vep.framework.utils.JsonProtocol

case class PlayView(
  id: String,
  theater: Theater,
  date: LocalDateTime,
  reservationEndDate: LocalDateTime,
  prices: Seq[PlayPrice]
)

object PlayView {
  import definiti.native.JsonSpraySupport._

  def apply(play: Play, theater: Theater): PlayView = {
    new PlayView(play.id, theater, play.date, play.reservationEndDate, play.prices)
  }

  implicit val playViewFormat: RootJsonFormat[PlayView] = jsonFormat5(PlayView.apply)
}

case class PlayMeta(
  id: String,
  date: LocalDateTime,
  show: String,
  showId: String,
  company: String
)

object PlayMeta {
  import definiti.native.JsonSpraySupport._

  implicit val playMetaFormat: RootJsonFormat[PlayMeta] = jsonFormat5(PlayMeta.apply)
}

case class PlayWithDependencies(
  play: Play,
  show: Show,
  company: Company,
  theater: Theater
)

object PlayWithDependencies {
  import JsonProtocol._

  implicit val playWithDependenciesFormat: RootJsonFormat[PlayWithDependencies] = jsonFormat4(PlayWithDependencies.apply)

}