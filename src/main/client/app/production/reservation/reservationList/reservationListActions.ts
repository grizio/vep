import { Action } from 'fluxx'
import {Reservation} from "../reservationModel";

export const initialize = Action<string>("initialize")

export const reservationLoaded = Action<Array<Reservation>>("reservationLoaded")