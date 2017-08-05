import { Action } from 'fluxx'
import {PeriodAdhesion, Adhesion} from "../adhesionModel";

export const initialize = Action<{period: PeriodAdhesion, adhesions: Array<Adhesion>}>("initialize")