import {LocalStore} from "fluxx"
import * as actions from "./homeActions"
import {copy} from "../../framework/utils/object";
import {Blog} from "../blog/blogModel";

export interface HomeState {
  blogs: Array<Blog>
}

const initialState: HomeState = {
  blogs: null
}

export const homeStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, blogs) => {
    return copy(state, {blogs})
  })
})