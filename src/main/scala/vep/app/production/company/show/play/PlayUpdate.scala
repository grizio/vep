package vep.app.production.company.show.play

import java.time.LocalDateTime

import spray.json.RootJsonFormat

case class PlayUpdate(
  id: String,
  theater: String,
  date: LocalDateTime,
  reservationEndDate: LocalDateTime,
  prices: Seq[PlayPrice]
)

object PlayUpdate {
  import vep.framework.utils.JsonProtocol._

  implicit val playUpdateFormat: RootJsonFormat[PlayUpdate] = jsonFormat5(PlayUpdate.apply)
}
