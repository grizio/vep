import { Action } from 'fluxx'
import {PlayWithDependencies} from "../playModel";

export const updateList = Action<Array<PlayWithDependencies>>("updateList")