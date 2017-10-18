package vep.app.production.company.show.play

import java.util.UUID

import scalikejdbc._
import vep.app.production.company.Company
import vep.app.production.company.show.Show
import vep.app.production.theater.{Seat, Theater, TheaterService}
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Invalid, Valid, Validation}

class PlayService(
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

  private def findSeatsByPlay(play: Play)(implicit session: DBSession): Seq[Seat] = {
    sql"""
      SELECT * FROM play_theater_seat
      WHERE play_id = ${play.id}
    """
      .map(Seat.apply)
      .list()
      .apply()
  }

  def find(id: String): Option[Play] = withQueryConnection { implicit session =>
    sql"""
      SELECT * FROM play
      WHERE id = ${id}
    """
      .map(Play.apply)
      .single()
      .apply()
  }

  def findFromShow(show: Show, id: String): Option[PlayView] = withQueryConnection { implicit session =>
    findPlayFromShow(show, id)
      .map(play => play.copy(prices = findPricesByPlay(play)))
      .flatMap { play =>
        theaterService.find(play.theater)
          .map(theater => theater.copy(seats = findSeatsByPlay(play)))
          .map(PlayView(play, _))
      }
  }

  def findFromShowWithDependencies(show: Show, id: String): Option[PlayWithDependencies] = withQueryConnection { implicit session =>
    findWithDependencies(id).filter(_.show.id == show.id)
  }

  private def findPlayFromShow(show: Show, id: String)(implicit session: DBSession): Option[Play] = {
    sql"""
      SELECT * FROM play
      WHERE id = ${id}
      AND   show = ${show.id}
    """
      .map(Play.apply)
      .single()
      .apply()
  }

  private def findPlay(id: String)(implicit session: DBSession): Option[Play] = {
    sql"""
      SELECT * FROM play
      WHERE id = ${id}
    """
      .map(Play.apply)
      .single()
      .apply()
  }

  def findAllFromShow(show: Show): Seq[PlayView] = withQueryConnection { implicit session =>
    findAllPlaysFromShow(show)
      .map(play => play.copy(prices = findPricesByPlay(play)))
      .flatMap(play => theaterService.find(play.theater).map(PlayView(play, _)))
  }

  private def findAllPlaysFromShow(show: Show)(implicit session: DBSession): Seq[Play] = {
    sql"""
      SELECT * FROM play
      WHERE show = ${show.id}
    """
      .map(Play.apply)
      .list()
      .apply()
  }


  def findNext(): Seq[PlayMeta] = withQueryConnection { implicit session =>
    findNextPlaysMeta()
  }

  private def findNextPlaysMeta()(implicit session: DBSession): Seq[PlayMeta] = {
    sql"""
      SELECT p.id, p.date, s.title, s.id as showId, c.id as companyId FROM play p
      JOIN show s ON s.id = p.show
      JOIN company c ON c.id = s.company
      WHERE date > CURRENT_TIMESTAMP
      ORDER BY date ASC
    """
      .map(PlayMeta.apply)
      .list()
      .apply()
  }

  def findNextFull(): Seq[PlayWithDependencies] = withQueryConnection { implicit session =>
    findNextPlay()
      .map(play => play.copy(prices = findPricesByPlay(play)))
      .map(includeDependencies)
      .flatten
  }

  def findNextPlay()(implicit session: DBSession): Seq[Play] = {
    sql"""
      SELECT p.* FROM play p
      JOIN show s ON s.id = p.show
      JOIN company c ON c.id = s.company
      WHERE date > CURRENT_TIMESTAMP
      ORDER BY date ASC
    """
      .map(Play.apply)
      .list()
      .apply()
  }

  def findWithDependencies(id: String): Option[PlayWithDependencies] = withQueryConnection { implicit session =>
    findPlay(id).flatMap(includeDependencies)
  }

  private def includeDependencies(play: Play)(implicit session: DBSession): Option[PlayWithDependencies] = {
    findShowByPlay(play).flatMap { show =>
      findCompanyByShow(show).flatMap { company =>
        findTheaterByPlay(play).map { theater =>
          PlayWithDependencies(play, show, company, theater)
        }
      }
    }
  }

  private def findShowByPlay(play: Play)(implicit session: DBSession): Option[Show] = {
    sql"""
      SELECT * FROM show s
      WHERE EXISTS(
        SELECT 1 FROM play p
        WHERE p.id = ${play.id} AND p.show = s.id
      )
    """
      .map(Show.apply)
      .single()
      .apply()
  }

  private def findCompanyByShow(show: Show)(implicit session: DBSession): Option[Company] = {
    sql"""
      SELECT * FROM company c
      WHERE EXISTS(
        SELECT 1 FROM show s
        WHERE s.id = ${show.id} AND s.company = c.id
      )
    """
      .map(Company.apply)
      .single()
      .apply()
  }

  private def findTheaterByPlay(play: Play)(implicit session: DBSession): Option[Theater] = {
    sql"""
      SELECT * FROM theater t
      WHERE id = ${play.theater}
    """
      .map(Theater.apply)
      .single()
      .apply()
  }

  def create(play: Play, show: Show): Validation[Play] = withCommandTransaction { implicit session =>
    insertPlay(play, show)
    play.prices.foreach(insertPrice(_, play))
    duplicateTheaterForPlay(play.id, play.theater)
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

  private def duplicateTheaterForPlay(playId: String, theaterId: String)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO play_theater_seat(play_id, c, x, y, w, h, t)
      SELECT ${playId}, c, x, y, w, h, t
      FROM theater_seat
      WHERE theater_id = ${theaterId}
    """
      .execute()
      .apply()
  }

  def update(play: Play): Validation[Play] = withCommandTransaction { implicit session =>
    val savedPlay = findPlay(play.id)
    lazy val theaterIsUpdated = savedPlay.exists(_.theater != play.theater)
    lazy val existingReservation = savedPlay.exists(p => existsReservedSeatsFromPlay(p.id))
    if (theaterIsUpdated && existingReservation) {
      Invalid(PlayMessages.existingReservation)
    } else {
      updatePlay(play)
      removePricesFromPlay(play.id)
      play.prices.foreach(insertPrice(_, play))
      if (theaterIsUpdated) {
        deleteTheaterFromPlay(play.id)
        duplicateTheaterForPlay(play.id, play.theater)
      }
      Valid(play)
    }
  }

  private def existsReservedSeatsFromPlay(playId: String)(implicit session: DBSession): Boolean = {
    sql"""
      SELECT COUNT(*) as c FROM reservation_seat
      WHERE play_id = ${playId}
    """
      .map(_.long("c"))
      .single()
      .apply()
      .exists(_ > 1)
  }

  private def deleteTheaterFromPlay(playId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM play_theater_seat
      WHERE play_id = ${playId}
    """
      .execute()
      .apply()
  }

  private def updatePlay(play: Play)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE play
      SET theater = ${play.theater},
          date = ${play.date},
          reservationEndDate = ${play.reservationEndDate}
       WHERE id = ${play.id}
    """
      .execute()
      .apply()
  }

  private def removePricesFromPlay(playId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM play_price
      WHERE play = ${playId}
    """
      .execute()
      .apply()
  }

  def delete(companyId: String, showId: String, playId: String): Validation[Unit] = withCommandTransaction { implicit session =>
    removePricesFromPlay(playId)
    deletePlay(companyId, showId, playId)
    Valid()
  }

  private def deletePlay(companyId: String, showId: String, playId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM play
      WHERE id = ${playId}
      AND show = ${showId}
      AND EXISTS(SELECT 1 FROM show WHERE id = ${showId} AND company = ${companyId})
    """
      .execute()
      .apply()
  }
}