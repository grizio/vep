import {LocalStore} from "fluxx"
import * as actions from "./playPageActions"
import {copy} from "../../../framework/utils/object";
import * as arrays from "../../../framework/utils/arrays";
import {Company} from "../../company/companyModel";
import {Show} from "../../show/showModel";
import {Play} from "../playModel";

export interface PlayPageState {
  company: Company
  show: Show
  play: Play
  otherPlays: Array<Play>
}

const initialState: PlayPageState = {
  company: null,
  show: null,
  play: null,
  otherPlays: null
}

export const playPageStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {company, show, play, playsFromShow}) => {
    return copy(state, {
      company, show, play,
      otherPlays: arrays.sort(playsFromShow.filter(p => p.id !== play.id), (p1, p2) => p1.date.getTime() - p2.date.getTime())
    })
  })
})