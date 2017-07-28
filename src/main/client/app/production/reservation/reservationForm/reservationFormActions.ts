import { Action } from 'fluxx'

export const initialize = Action<string>("initialize")

export const reloadReservedSeats = Action<Array<string>>("initialize")

export const updateFirstName = Action<string>("updateFirstName")

export const updateLastName = Action<string>("updateLastName")

export const updateEmail = Action<string>("updateEmail")

export const updateCity = Action<string>("updateCity")

export const updateComment = Action<string>("updateComment")

export const toggleSeat = Action<string>("toggleSeat")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")

export const closeSuccess = Action("closeSuccess")