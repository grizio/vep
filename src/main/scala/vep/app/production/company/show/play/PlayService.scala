package vep.app.production.company.show.play

import java.util.UUID

import scalikejdbc._
import vep.Configuration
import vep.app.production.company.show.Show
import vep.app.production.theater.TheaterService
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class PlayService(
  val configuration: Configuration,
  theaterService: TheaterService
) extends DatabaseContainer {
  def findByShow(show: Show): Seq[PlayView] = withQueryConnection { implicit session =>
    findPlaysByShow(show)
      .map(play => play.copy(prices = findPricesByPlay(play)))
      .flatMap(play => theaterService.find(play.theater).map(PlayView(play, _)))
  }

  private def findPlaysByShow(show: Show)(implicit session: DBSession): Seq[Play] = {
    sql"""
      SELECT * FROM play
      WHERE show = ${show.id}
    """
      .map(Play.apply)
      .list()
      .apply()
  }

  private def findPricesByPlay(play: Play)(implicit session: DBSession): Seq[PlayPrice] = {
    sql"""
      SELECT * FROM play_price
      WHERE play = ${play.id}
    """
      .map(PlayPrice.apply)
      .list()
      .apply()
  }

  def create(play: Play, show: Show): Validation[Play] = withCommandTransaction { implicit session =>
    insertPlay(play, show)
    play.prices.foreach(insertPrice(_, play))
    Valid(play)
  }

  private def insertPlay(play: Play, show: Show)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO play(id, theater, date, reservationEndDate, show)
      VALUES (${play.id}, ${play.theater}, ${play.date}, ${play.reservationEndDate}, ${show.id})
    """
      .execute()
      .apply()
  }

  private def insertPrice(price: PlayPrice, play: Play)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO play_price(id, name, value, condition, play)
      VALUES (${UUID.randomUUID().toString}, ${price.name}, ${price.value}, ${price.condition}, ${play.id})
    """
      .execute()
      .apply()
  }
}