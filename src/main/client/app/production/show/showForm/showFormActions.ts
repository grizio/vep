import { Action } from 'fluxx'
import {Company} from "../../company/companyModel";
import {Show} from "../showModel";

export const initialize = Action<{company: Company, show?: Show}>("initialize")

export const updateTitle = Action<string>("updateTitle")

export const updateAuthor = Action<string>("updateAuthor")

export const updateDirector = Action<string>("updateDirector")

export const updateContent = Action<string>("updateContent")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")