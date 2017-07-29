import { Action } from 'fluxx'
import {ShowWithDependencies} from "../showModel";

export const updateList = Action<Array<ShowWithDependencies>>("updateList")