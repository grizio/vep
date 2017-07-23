import { Action } from 'fluxx'
import {PlayMeta, ShowMeta} from "../production/company/companyModel";
import {SessionState} from "../framework/session/sessionStore";

export const initialize = Action<{shows: Array<ShowMeta>, plays: Array<PlayMeta>}>("initialize")

export const updateSession = Action<SessionState>("updateSession")