package vep.app.production.theater

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class TheaterService(
  val configuration: Configuration
) extends DatabaseContainer {
  def create(theater: Theater): Validation[Theater] = withCommandTransaction { implicit session =>
    insertTheater(theater)
    theater.seats.foreach(insertSeat(_, theater.id))
    Valid(theater)
  }

  def insertTheater(theater: Theater)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO theater(id, name, address, content)
      VALUES (${theater.id}, ${theater.name}, ${theater.address}, ${theater.content})
    """
      .execute()
      .apply()
  }

  def insertSeat(seat: Seat, theaterId: String)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO theater_seat(theater_id, c, x, y, w, h, t)
      VALUES(${theaterId}, ${seat.c}, ${seat.x}, ${seat.y}, ${seat.w}, ${seat.h}, ${seat.t})
    """
      .execute()
      .apply()
  }
}
