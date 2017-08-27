import {LocalStore} from "fluxx"
import * as actions from "./homeActions"
import {copy} from "../../framework/utils/object";
import {PageInformation} from "../pages/pageModel";

export interface HomeState {
  page: PageInformation
}

const initialState: HomeState = {
  page: null
}

export const homeStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, page) => {
    return copy(state, {page})
  })
})