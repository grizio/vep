import {LocalStore} from "fluxx"
import * as actions from "./theaterListActions"
import {copy} from "../../../framework/utils/object";
import {Theater} from "../theaterModel";

export interface TheaterListState {
  theaters: Array<Theater>
}

const initialState: TheaterListState = {
  theaters: []
}

export const theaterListStore = () => LocalStore(initialState, on => {
  on(actions.updateList, (state, theaters) => {
    return copy(state, {theaters})
  })
})