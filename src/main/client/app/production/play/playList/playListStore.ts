import {LocalStore} from "fluxx"
import {copy} from "../../../framework/utils/object";
import {PlayWithDependencies} from "../playModel";
import * as actions from "./playListActions"

export interface PlayListState {
  plays: Array<PlayWithDependencies>
}

const initialState: PlayListState = {
  plays: null
}

export const playListStore = () => LocalStore(initialState, on => {
  on(actions.updateList, (state, plays) => {
    return copy(state, {plays})
  })
})