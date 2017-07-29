import {LocalStore} from "fluxx"
import * as actions from "./NavigationActions"
import {copy} from "../framework/utils/object";
import {SessionState, sessionStore} from "../framework/session/sessionStore";
import {PlayMeta} from "../production/play/playModel";
import {ShowMeta} from "../production/show/showModel";

export interface NavigationState {
  shows: Array<ShowMeta>
  plays: Array<PlayMeta>
  session: SessionState
}

const initialState: NavigationState = {
  shows: [],
  plays: [],
  session: null
}

export const navigationStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {shows, plays}) => {
    return copy(state, {shows, plays})
  })

  on(actions.updateSession, (state, session) => {
    return copy(state, {session})
  })
})

sessionStore.subscribe(actions.updateSession)