import {LocalStore} from "fluxx"
import * as actions from "./pageReadActions"
import {copy} from "../../../framework/utils/object";
import {PageInformation} from "../pageModel";

export interface PageReadState {
  page: PageInformation
}

const initialState: PageReadState = {
  page: null
}

export const pageReadStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, page) => {
    return copy(state, {page})
  })
})