import {LocalStore} from "fluxx"
import * as actions from "./reservationListActions"
import {copy} from "../../../framework/utils/object";
import {Reservation} from "../reservationModel";

export interface ReservationListState {
  loading: boolean
  reservations: Array<Reservation>
}

const initialState: ReservationListState = {
  loading: true,
  reservations: null
}

export const reservationListStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {reservations}) => {
    return copy(state, {reservations})
  })
})