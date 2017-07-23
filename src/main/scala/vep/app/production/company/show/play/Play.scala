package vep.app.production.company.show.play

import org.joda.time.DateTime
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Play(
  id: String,
  theater: String,
  date: DateTime,
  reservationEndDate: DateTime,
  prices: Seq[PlayPrice]
)

object Play {
  import JsonProtocol._

  implicit val playFormat: RootJsonFormat[Play] = jsonFormat5(Play.apply)
}

case class PlayPrice(
  name: String,
  value: BigDecimal,
  condition: String
)

object PlayPrice {
  import JsonProtocol._

  implicit val playPriceFormat: RootJsonFormat[PlayPrice] = jsonFormat3(PlayPrice.apply)
}

case class PlayCreation(
  theater: String,
  date: DateTime,
  reservationEndDate: DateTime,
  prices: Seq[PlayPrice]
)

object PlayCreation {
  import JsonProtocol._

  implicit val playCreationFormat: RootJsonFormat[PlayCreation] = jsonFormat4(PlayCreation.apply)
}