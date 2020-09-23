package vep.app.production.company.show.play

import java.time.LocalDateTime

import org.joda.time.DateTime
import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.app.production.company.Company
import vep.app.production.company.show.Show
import vep.app.production.theater.Theater
import vep.framework.utils.JsonProtocol

case class Play(
  id: String,
  theater: String,
  date: LocalDateTime,
  reservationEndDate: LocalDateTime,
  prices: Seq[PlayPrice],
  anonymized: Boolean
)

object Play {

  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): Play = {
    new Play(
      id = resultSet.string("id"),
      theater = resultSet.string("theater"),
      date = resultSet.localDateTime("date"),
      reservationEndDate = resultSet.localDateTime("reservationEndDate"),
      prices = Seq.empty,
      anonymized = resultSet.boolean("anonymized")
    )
  }

  implicit val playFormat: RootJsonFormat[Play] = jsonFormat6(Play.apply)
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
  date: LocalDateTime,
  reservationEndDate: LocalDateTime,
  prices: Seq[PlayPrice]
)

object PlayCreation {

  import JsonProtocol._

  implicit val playCreationFormat: RootJsonFormat[PlayCreation] = jsonFormat4(PlayCreation.apply)
}

case class PlayView(
  id: String,
  theater: Theater,
  date: LocalDateTime,
  reservationEndDate: LocalDateTime,
  prices: Seq[PlayPrice]
)

object PlayView {

  import JsonProtocol._

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

  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): PlayMeta = {
    new PlayMeta(
      id = resultSet.string("id"),
      date = resultSet.localDateTime("date"),
      show = resultSet.string("title"),
      showId = resultSet.string("showId"),
      company = resultSet.string("companyId")
    )
  }

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