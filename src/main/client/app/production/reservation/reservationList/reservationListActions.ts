import { Action } from 'fluxx'
import {Reservation} from "../reservationModel";

export const initialize = Action<{reservations: Array<Reservation>}>("initialize")