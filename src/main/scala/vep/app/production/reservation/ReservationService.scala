package vep.app.production.reservation

import scalikejdbc._
import vep.app.production.company.show.play.Play
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class ReservationService(
) extends DatabaseContainer {
  import ReservationService._

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
      .map(reservation => reservation.copy(
        seats = findSeatsByReservation(reservation),
        prices = findPricesByReservation(reservation)
      ))
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
      .map(toReservation)
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

  private def findPricesByReservation(reservation: Reservation)(implicit session: DBSession): Seq[ReservationPrice] = {
    sql"""
      SELECT *
      FROM reservation_price
      WHERE reservation_id = ${reservation.id}
    """
      .map(toReservationPrice)
      .list()
      .apply()
  }

  def findByPlay(reservationId: String, play: Play): Option[Reservation] = withQueryConnection { implicit session =>
    findReservationByPlay(reservationId, play.id)
      .map(reservation => reservation.copy(
        seats = findSeatsByReservation(reservation),
        prices = findPricesByReservation(reservation)
      ))
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
      .map(toReservation)
      .single()
      .apply()
  }

  def create(reservation: Reservation, playId: String): Validation[Reservation] = withCommandTransaction { implicit session =>
    insertReservation(reservation)
    reservation.seats.foreach(insertReservationSeat(_, reservation, playId))
    reservation.prices.foreach(insertReservationPrice(_, reservation))
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

  private def insertReservationPrice(price: ReservationPrice, reservation: Reservation)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO reservation_price(reservation_id, price, seatsCount)
      VALUES (${reservation.id}, ${price.price}, ${price.count})
    """
      .execute()
      .apply()
  }

  def delete(playId: String, reservationId: String): Validation[Unit] = withCommandTransaction { implicit session =>
    if (findReservationByPlay(reservationId, playId).isDefined) {
      deleteSeatsByReservation(reservationId)
      deletePricesByReservation(reservationId)
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

  private def deletePricesByReservation(reservationId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM reservation_price
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

  def deleteFromPlay(playId: String): Validation[Unit] = withCommandTransaction { implicit session =>
    findReservationFromPlay(playId)
      .foreach { reservation =>
        deleteSeatsByReservation(reservation.id)
        deleteReservation(reservation.id)
      }
    Valid()
  }
}

object ReservationService {
  def toReservation(resultSet: WrappedResultSet): Reservation = {
    new Reservation(
      id = resultSet.string("id"),
      firstName = resultSet.string("first_name"),
      lastName = resultSet.string("last_name"),
      email = resultSet.string("email"),
      city = resultSet.stringOpt("city"),
      comment = resultSet.stringOpt("comment"),
      seats = List.empty,
      prices = List.empty
    )
  }

  def toReservationPrice(resultSet: WrappedResultSet): ReservationPrice = {
    new ReservationPrice(
      price = resultSet.string("price"),
      count = resultSet.int("seatsCount")
    )
  }
}