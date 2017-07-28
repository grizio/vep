import {request} from "../../framework/utils/http";
import {Reservation, ReservationCreation} from "./reservationModel";

export function findReservedSeats(play: string): Promise<Array<string>> {
  return request({
    method: "GET",
    url: `production/reservations/${play}/reservedSeats`
  })
}

export function findReservations(play: string): Promise<Array<Reservation>> {
  return request({
    method: "GET",
    url: `production/reservations/${play}`
  })
}

export function createReservation(play: string, reservation: ReservationCreation) {
  return request({
    method: "POST",
    url: `production/reservations/${play}`,
    entity: reservation
  })
}

export function deleteReservation(play: string, reservation: Reservation) {
  return request({
    method: "DELETE",
    url: `production/reservations/${play}/${reservation.id}`
  })
}