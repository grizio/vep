package vep.app.production.company.show.play

import java.util.UUID
import scalikejdbc._
import vep.Configuration
import vep.app.production.company.show.Show
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class PlayService(
  val configuration: Configuration
) extends DatabaseContainer {
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