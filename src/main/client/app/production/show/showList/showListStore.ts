import {LocalStore} from "fluxx"
import {copy} from "../../../framework/utils/object";
import * as actions from "./showListActions"
import {ShowWithDependencies} from "../showModel";

export interface ShowListState {
  nextShows: Array<ShowWithDependencies>
  previousShows: Array<ShowWithDependencies>
}

const initialState: ShowListState = {
  nextShows: null,
  previousShows: null
}

export const showListStore = () => LocalStore(initialState, on => {
  on(actions.updateList, (state, shows) => {
    return copy(state, {
      nextShows: shows.filter(_ => _.plays && _.plays.length),
      previousShows: shows.filter(_ => !(_.plays && _.plays.length))
    })
  })
})