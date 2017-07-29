import { Action } from 'fluxx'
import {Company} from "../../company/companyModel";
import {Show} from "../showModel";
import {Play} from "../../play/playModel";

export const initialize = Action<{company: Company, show: Show, plays: Array<Play>}>("initialize")