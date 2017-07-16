import { Action } from 'fluxx'
import {Theater} from "../theaterModel";

export const updateList = Action<Array<Theater>>("updateList")