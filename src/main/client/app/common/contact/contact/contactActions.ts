import { Action } from 'fluxx'

export const updateName = Action<string>("updateName")

export const updateEmail = Action<string>("updateEmail")

export const updateEmail2 = Action<string>("updateEmail2")

export const updateTitle = Action<string>("updateTitle")

export const updateContent = Action<string>("updateContent")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")