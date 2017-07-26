package vep.app.production.reservation

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Invalid, Valid, Validation}

class ReservationService(
  val configuration: Configuration
) extends DatabaseContainer {
  def findReservedSeats(playId: String): Seq[String] = withQueryConnection { implicit session =>
    findReservedSeatsFromPlay(playId)
  }

  private def findReservedSeatsFromPlay(playId: String)(implicit session: DBSession): Seq[String] = {
    sql"""
      SELECT play_c
      FROM reservation_seat
      WHERE play_id = ${playId}
    """
      .map(resultSet => resultSet.string("play_c"))
      .list()
      .apply()
  }

  def create(reservation: Reservation, playId: String): Validation[Reservation] = withCommandTransaction { implicit session =>
    insertReservation(reservation)
    reservation.seats.foreach(insertReservationSeat(_, reservation, playId))
    Valid(reservation)
  }

  private def insertReservation(reservation: Reservation)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO reservation(id, first_name, last_name, email, city, comment)
      VALUES (
        ${reservation.id},
        ${reservation.firstName},
        ${reservation.lastName},
        ${reservation.email},
        ${reservation.city},
        ${reservation.comment}
      )
    """
      .execute()
      .apply()
  }

  private def insertReservationSeat(seat: String, reservation: Reservation, playId: String)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO reservation_seat(reservation_id, play_id, play_c)
      VALUES (${reservation.id}, ${playId}, ${seat})
    """
      .execute()
      .apply()
  }
}