import { Action } from 'fluxx'
import {Company} from "../../company/companyModel";
import {Show} from "../../show/showModel";
import {Play} from "../playModel";

export const initialize = Action<{company: Company, show: Show, play: Play, playsFromShow: Array<Play>}>("initialize")