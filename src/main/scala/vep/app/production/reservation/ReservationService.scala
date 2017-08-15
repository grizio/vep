package vep.app.production.reservation

import scalikejdbc._
import vep.app.production.company.show.play.Play
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class ReservationService(
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

  def findAllByPlay(playId: String): Seq[Reservation] = withQueryConnection { implicit session =>
    findReservationFromPlay(playId)
      .map(reservation => reservation.copy(seats = findSeatsByReservation(reservation)))
  }

  private def findReservationFromPlay(playId: String)(implicit session: DBSession): Seq[Reservation] = {
    sql"""
      SELECT *
      FROM reservation r
      WHERE EXISTS(
        SELECT 1 FROM reservation_seat rs
        WHERE rs.reservation_id = r.id
        AND play_id = ${playId}
      )
    """
      .map(Reservation.apply)
      .list()
      .apply()
  }

  private def findSeatsByReservation(reservation: Reservation)(implicit session: DBSession): Seq[String] = {
    sql"""
      SELECT play_c
      FROM reservation_seat
      WHERE reservation_id = ${reservation.id}
    """
      .map(_.string("play_c"))
      .list()
      .apply()
  }

  def findByPlay(reservationId: String, play: Play): Option[Reservation] = withQueryConnection { implicit session =>
    findReservationByPlay(reservationId, play.id)
      .map(reservation => reservation.copy(seats = findSeatsByReservation(reservation)))
  }

  private def findReservationByPlay(reservationId: String, playId: String)(implicit session: DBSession): Option[Reservation] = {
    sql"""
      SELECT *
      FROM reservation r
      WHERE id = ${reservationId}
      AND EXISTS(
        SELECT 1 FROM reservation_seat rs
        WHERE rs.reservation_id = r.id
        AND play_id = ${playId}
      )
    """
      .map(Reservation.apply)
      .single()
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

  def delete(playId: String, reservationId: String): Validation[Unit] = withCommandTransaction { implicit session =>
    if (findReservationByPlay(reservationId, playId).isDefined) {
      deleteSeatsByReservation(reservationId)
      deleteReservation(reservationId)
    }
    Valid()
  }

  private def deleteSeatsByReservation(reservationId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM reservation_seat
      WHERE reservation_id = ${reservationId}
    """
      .execute()
      .apply()
  }

  private def deleteReservation(reservationId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM reservation
      WHERE id = ${reservationId}
    """
      .execute()
      .apply()
  }
}