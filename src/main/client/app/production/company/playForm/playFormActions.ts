import { Action } from 'fluxx'
import {Company, Show} from "../companyModel";
import {Theater} from "../../theater/theaterModel";

export const initialize = Action<{company: Company, show: Show, theaters: Array<Theater>}>("initialize")

export const updateTheater = Action<string>("updateTheater")

export const updateDate = Action<Date>("updateDate")

export const updateReservationEndDate = Action<Date>("updateReservationEndDate")

export const addPrice = Action("addPrice")

export const removePrice = Action<number>("removePrice")

export const updatePriceName = Action<{index: number, value: string}>("updatePriceName")

export const updatePriceValue = Action<{index: number, value: number}>("updatePriceValue")

export const updatePriceCondition = Action<{index: number, value: string}>("updatePriceCondition")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")