import { Action } from 'fluxx'
import {SessionState} from "../framework/session/sessionStore";
import {ShowMeta} from "../production/show/showModel";
import {PlayMeta} from "../production/play/playModel";

export const initialize = Action<{shows: Array<ShowMeta>, plays: Array<PlayMeta>}>("initialize")

export const updateSession = Action<SessionState>("updateSession")