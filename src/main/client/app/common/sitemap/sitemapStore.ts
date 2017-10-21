import {LocalStore} from "fluxx"
import * as actions from "./sitemapActions"
import {ShowWithDependencies} from "../../production/show/showModel"
import {copy} from "../../framework/utils/object"
import {PageInformation} from "../pages/pageModel"

export interface SitemapState {
  pages: Array<PageInformation>,
  shows: Array<ShowWithDependencies>
}

const initialState: SitemapState = {
  pages: [],
  shows: []
}

export const sitemapStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {pages, shows}) => {
    return copy(state, {pages, shows})
  })
})