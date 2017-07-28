import {LocalStore} from "fluxx"
import * as actions from "./reservationListActions"
import {copy} from "../../../framework/utils/object";
import {Reservation} from "../reservationModel";
import {reservationDeleted, reservationDone} from "../reservationActions";
import {findReservations} from "../reservationApi";

export interface ReservationListState {
  play: string
  loading: boolean
  reservations: Array<Reservation>
}

const initialState: ReservationListState = {
  play: null,
  loading: true,
  reservations: null
}

export const reservationListStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, playId) => {
    reloadReservations(playId)
    return copy(state, {play: playId})
  })

  on(actions.reservationLoaded, (state, reservations) => {
    return copy(state, {reservations})
  })

  on(reservationDone, (state) => {
    reloadReservations(state.play)
    return state
  })

  on(reservationDeleted, (state) => {
    reloadReservations(state.play)
    return state
  })
})

function reloadReservations(playId: string) {
  findReservations(playId)
    .then(reservations => actions.reservationLoaded(reservations))
}