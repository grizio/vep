import { Action } from 'fluxx'
import {Profile} from "../profileModel";

export const initialize = Action<Profile>("initialize")

export const updateFirstName = Action<string>("updateFirstName")

export const updateLastName = Action<string>("updateLastName")

export const updateAddress = Action<string>("updateAddress")

export const updateZipCode = Action<string>("updateZipCode")

export const updateCity = Action<string>("updateCity")

export const addPhone = Action("addPhone")

export const removePhone = Action<number>("removePhone")

export const updatePhoneName = Action<{index: number, value: string}>("updatePhoneName")

export const updatePhoneNumber = Action<{index: number, value: string}>("updatePhoneNumber")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")