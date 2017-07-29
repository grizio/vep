import { Action } from 'fluxx'
import {Theater} from "../../theater/theaterModel";
import {Company} from "../../company/companyModel";
import {Show} from "../../show/showModel";
import {Play} from "../playModel";

export const initialize = Action<{company: Company, show: Show, play?: Play, theaters: Array<Theater>}>("initialize")

export const updateTheater = Action<string>("updateTheater")

export const addDate = Action("addDate")

export const removeDate = Action<number>("removeDate")

export const updateDate = Action<{index: number, value: Date}>("updateDate")

export const updateReservationEndDate = Action<{index: number, value: Date}>("updateReservationEndDate")

export const addPrice = Action("addPrice")

export const removePrice = Action<number>("removePrice")

export const updatePriceName = Action<{index: number, value: string}>("updatePriceName")

export const updatePriceValue = Action<{index: number, value: number}>("updatePriceValue")

export const updatePriceCondition = Action<{index: number, value: string}>("updatePriceCondition")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")