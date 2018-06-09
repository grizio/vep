import { Action } from 'fluxx'
import { Profile } from '../profileModel'

export const initialize = Action<Profile>('initialize')

export const updatePassword = Action<string>('updatePassword')

export const updateErrors = Action<Array<string>>("updateErrors")

export const closeErrors = Action('closeErrors')