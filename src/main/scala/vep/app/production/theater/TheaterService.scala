package vep.app.production.theater

import scalikejdbc._
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class TheaterService() extends DatabaseContainer {
  import TheaterService._

  def findAll(): Seq[Theater] = withQueryConnection { implicit session =>
    findAllTheaters()
      .map(theater => theater.copy(seats = findSeatsByTheater(theater.id).toList))
  }

  private def findAllTheaters()(implicit session: DBSession): Seq[Theater] = {
    sql"""
      SELECT * FROM theater
    """
      .map(toTheater)
      .list()
      .apply()
  }

  private def findSeatsByTheater(theaterId: String)(implicit session: DBSession): Seq[Seat] = {
    sql"""
      SELECT * FROM theater_seat
      WHERE theater_id = ${theaterId}
    """
      .map(toSeat)
      .list()
      .apply()
  }

  def find(id: String): Option[Theater] = withQueryConnection { implicit session =>
    findTheater(id)
      .map(theater => theater.copy(seats = findSeatsByTheater(theater.id).toList))
  }

  private def findTheater(id: String)(implicit session: DBSession): Option[Theater] = {
    sql"""
      SELECT * FROM theater
      WHERE id = $id
    """
      .map(toTheater)
      .single()
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

  def update(theater: Theater): Validation[Theater] = withCommandTransaction { implicit session =>
    updateTheater(theater)
    removeAllSeats(theater.id)
    theater.seats.foreach(insertSeat(_, theater.id))
    Valid(theater)
  }

  private def updateTheater(theater: Theater): Unit = withCommandTransaction { implicit session =>
    sql"""
      UPDATE theater SET
       name = ${theater.name},
       address = ${theater.address},
       content = ${theater.content}
      WHERE
        id = ${theater.id}
    """
      .execute()
      .apply()
  }

  private def removeAllSeats(theaterId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM theater_seat
      WHERE theater_id = ${theaterId}
    """
      .execute()
      .apply()
  }

  def delete(theaterId: String): Validation[Unit] = withCommandTransaction { implicit session =>
    removeAllSeats(theaterId)
    deleteTheater(theaterId)
    Valid()
  }

  private def deleteTheater(theaterId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM theater
      WHERE id = ${theaterId}
    """
      .execute()
      .apply()
  }
}

object TheaterService {
  def toTheater(resultSet: WrappedResultSet): Theater = {
    new Theater(
      id = resultSet.string("id"),
      name = resultSet.string("name"),
      address = resultSet.string("address"),
      content = resultSet.string("content"),
      seats = List.empty
    )
  }

  def toSeat(resultSet: WrappedResultSet): Seat = {
    new Seat(
      c = resultSet.string("c"),
      x = resultSet.int("x"),
      y = resultSet.int("y"),
      w = resultSet.int("w"),
      h = resultSet.int("h"),
      t = resultSet.string("t")
    )
  }
}