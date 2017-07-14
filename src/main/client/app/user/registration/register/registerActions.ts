import { Action } from 'fluxx'

export const updateEmail = Action<string>("updateEmail")

export const updatePassword = Action<string>("updatePassword")

export const updatePassword2 = Action<string>("updatePassword2")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")