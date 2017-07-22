import { Action } from 'fluxx'
import {Company} from "../companyModel";

export const updateList = Action<Array<Company>>("updateList")