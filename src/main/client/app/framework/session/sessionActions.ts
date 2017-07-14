import { Action } from 'fluxx'
import {UserSessionAuthorization} from "./sessionModel"
import {SessionUser} from "./sessionStore"

export const login = Action<UserSessionAuthorization>("login")

export const logout = Action("logout")

export const updateUser = Action<SessionUser>("updateUser")