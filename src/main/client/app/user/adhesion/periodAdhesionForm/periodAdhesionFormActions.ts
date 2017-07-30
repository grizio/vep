import { Action } from 'fluxx'
import {PeriodAdhesion} from "../adhesionModel";

export const initializeEmpty = Action("initializeEmpty")

export const initialize = Action<PeriodAdhesion>("initialize")

export const updateStart = Action<Date>("updateStart")

export const updateEnd = Action<Date>("updateEnd")

export const updateRegistrationStart = Action<Date>("updateRegistrationStart")

export const updateRegistrationEnd = Action<Date>("updateRegistrationEnd")

export const addActivity = Action("addActivity")

export const removeActivity = Action<number>("removeActivity")

export const updateActivity = Action<{index: number, value: string}>("updateActivity")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")