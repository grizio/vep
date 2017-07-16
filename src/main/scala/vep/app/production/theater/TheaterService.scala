package vep.app.production.theater

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class TheaterService(
  val configuration: Configuration
) extends DatabaseContainer {
  def findAll(): Seq[Theater] = withQueryConnection { implicit session =>
    findAllTheaters()
      .map(theater => theater.copy(seats = findSeatsByTheater(theater.id)))
  }

  private def findAllTheaters()(implicit session: DBSession): Seq[Theater] = {
    sql"""
      SELECT * FROM theater
    """
      .map(Theater.apply)
      .list()
      .apply()
  }

  private def findSeatsByTheater(theaterId: String)(implicit session: DBSession): Seq[Seat] = {
    sql"""
      SELECT * FROM theater_seat
      WHERE theater_id = ${theaterId}
    """
      .map(Seat.apply)
      .list()
      .apply()
  }

  def create(theater: Theater): Validation[Theater] = withCommandTransaction { implicit session =>
    insertTheater(theater)
    theater.seats.foreach(insertSeat(_, theater.id))
    Valid(theater)
  }

  private def insertTheater(theater: Theater)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO theater(id, name, address, content)
      VALUES (${theater.id}, ${theater.name}, ${theater.address}, ${theater.content})
    """
      .execute()
      .apply()
  }

  private def insertSeat(seat: Seat, theaterId: String)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO theater_seat(theater_id, c, x, y, w, h, t)
      VALUES(${theaterId}, ${seat.c}, ${seat.x}, ${seat.y}, ${seat.w}, ${seat.h}, ${seat.t})
    """
      .execute()
      .apply()
  }
}
