import { Action } from 'fluxx'
import {PeriodAdhesion} from "../adhesionModel";

export const initialize = Action<{acceptedPeriods: Array<PeriodAdhesion>}>("initialize")

export const updatePeriod = Action<string>("updatePeriod")

export const addMember = Action("addMember")

export const removeMember = Action<number>("removeMember")

export const updateMemberFirstName = Action<{index: number, value: string}>("updateMemberFirstName")

export const updateMemberLastName = Action<{index: number, value: string}>("updateMemberLastName")

export const updateMemberBirthday = Action<{index: number, value: Date}>("updateMemberBirthday")

export const updateMemberActivity = Action<{index: number, value: string}>("updateMemberActivity")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")