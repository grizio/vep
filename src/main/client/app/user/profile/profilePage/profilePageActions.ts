import { Action } from 'fluxx'
import {Profile} from "../profileModel";
import {Adhesion} from "../../adhesion/adhesionModel";

export const initialize = Action<{profile: Profile, adhesions: Array<Adhesion>}>("initialize")