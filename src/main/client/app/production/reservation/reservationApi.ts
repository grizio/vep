import {request} from "../../framework/utils/http";
import {ReservationCreation} from "./reservationForm/reservationModel";

export function findReservedSeats(play: string): Promise<Array<string>> {
  return request({
    method: "GET",
    url: `production/reservations/${play}/reservedSeats`
  })
}

export function createReservation(play: string, reservation: ReservationCreation) {
  return request({
    method: "POST",
    url: `production/reservations/${play}`,
    entity: reservation
  })
}