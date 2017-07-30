import { Action } from 'fluxx'
import {PeriodAdhesion} from "../adhesionModel";

export const updateList = Action<Array<PeriodAdhesion>>("updateList")