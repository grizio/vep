import { Action } from 'fluxx'
import {Company, Play, Show} from "../companyModel";

export const initialize = Action<{company: Company, show: Show, play: Play, playsFromShow: Array<Play>}>("initialize")