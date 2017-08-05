import { Action } from 'fluxx'
import {Company} from "../../company/companyModel";
import {Show} from "../showModel";
import {Play} from "../../play/playModel";
import {SessionState} from "../../../framework/session/sessionStore";

export const initialize = Action<{company: Company, show: Show, plays: Array<Play>}>("initialize")

export const updateSession = Action<SessionState>("updateSession")