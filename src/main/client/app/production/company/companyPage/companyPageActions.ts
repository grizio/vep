import { Action } from 'fluxx'
import {Company} from "../companyModel";
import {Show} from "../../show/showModel";

export const initialize = Action<{company: Company, shows: Array<Show>}>("initialize")