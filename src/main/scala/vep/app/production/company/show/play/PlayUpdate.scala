package vep.app.production.company.show.play

import org.joda.time.DateTime
import spray.json.RootJsonFormat

case class PlayUpdate(
  id: String,
  theater: String,
  date: DateTime,
  reservationEndDate: DateTime,
  prices: Seq[PlayPrice]
)

object PlayUpdate {
  import vep.framework.utils.JsonProtocol._

  implicit val playUpdateFormat: RootJsonFormat[PlayUpdate] = jsonFormat5(PlayUpdate.apply)
}
