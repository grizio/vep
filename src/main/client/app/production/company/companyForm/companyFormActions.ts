import { Action } from 'fluxx'
import {Company} from "../companyModel";

export const initializeEmpty = Action("initializeEmpty")

export const initialize = Action<Company>("initialize")

export const updateName = Action<string>("updateName")

export const updateAddress = Action<string>("updateAddress")

export const updateContent = Action<string>("updateContent")

export const updateIsVep = Action<boolean>("updateIsVep")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")