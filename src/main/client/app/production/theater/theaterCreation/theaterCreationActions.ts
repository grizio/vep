import { Action } from 'fluxx'

export const updateName = Action<string>("updateName")

export const updateAddress = Action<string>("updateAddress")

export const updateContent = Action<string>("updateContent")

export const addSeat = Action("addSeat")

export const removeSeat = Action<number>("removeSeat")

export const selectSeat = Action<number>("selectSeat")

export const updateSeatCode = Action<{index: number, value: string}>("updateSeatCode")

export const updateSeatX = Action<{index: number, value: number}>("updateSeatX")

export const updateSeatY = Action<{index: number, value: number}>("updateSeatY")

export const updateSeatWidth = Action<{index: number, value: number}>("updateSeatWidth")

export const updateSeatHeight = Action<{index: number, value: number}>("updateSeatHeight")

export const updateSeatType = Action<{index: number, value: string}>("updateSeatType")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")