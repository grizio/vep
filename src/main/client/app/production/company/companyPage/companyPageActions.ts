import { Action } from 'fluxx'
import {Company, Show} from "../companyModel";

export const initialize = Action<{company: Company, shows: Array<Show>}>("initialize")