import { Action } from 'fluxx'
import {Blog} from "../blogModel";

export const initialize = Action<Blog>("initialize")

export const initializeEmpty = Action("initializeEmpty")

export const updateTitle = Action<string>("updateTitle")

export const updateContent = Action<string>("updateContent")

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action("closeErrors")

export const success = Action("success")