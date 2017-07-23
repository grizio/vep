package vep.app.production.company.show.play

import org.joda.time.DateTime
import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.app.production.theater.Theater
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

  def apply(resultSet: WrappedResultSet): Play = {
    new Play(
      id = resultSet.string("id"),
      theater = resultSet.string("theater"),
      date = resultSet.jodaDateTime("date"),
      reservationEndDate = resultSet.jodaDateTime("reservationEndDate"),
      prices = Seq.empty
    )
  }

  implicit val playFormat: RootJsonFormat[Play] = jsonFormat5(Play.apply)
}

case class PlayPrice(
  name: String,
  value: BigDecimal,
  condition: String
)

object PlayPrice {
  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): PlayPrice = {
    new PlayPrice(
      name = resultSet.string("name"),
      value = resultSet.bigDecimal("value"),
      condition = resultSet.string("condition")
    )
  }

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

case class PlayView(
  id: String,
  theater: Theater,
  date: DateTime,
  reservationEndDate: DateTime,
  prices: Seq[PlayPrice]
)

object PlayView {
  import JsonProtocol._

  def apply(play: Play, theater: Theater): PlayView = {
    new PlayView(play.id, theater, play.date, play.reservationEndDate, play.prices)
  }

  implicit val playViewFormat: RootJsonFormat[PlayView] = jsonFormat5(PlayView.apply)
}