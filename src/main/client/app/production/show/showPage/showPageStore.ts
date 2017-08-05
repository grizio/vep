import {LocalStore} from "fluxx"
import * as actions from "./showPageActions"
import {copy} from "../../../framework/utils/object";
import {Company} from "../../company/companyModel";
import {Show} from "../showModel";
import {Play} from "../../play/playModel";
import {SessionState, sessionStore} from "../../../framework/session/sessionStore";

export interface ShowPageState {
  loading: boolean
  company: Company
  show: Show
  plays: Array<Play>
  session: SessionState
}

const initialState: ShowPageState = {
  loading: true,
  company: null,
  show: null,
  plays: [],
  session: sessionStore.state
}

export const showPageStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {company, show, plays}) => {
    return copy(state, {loading: false, company, show, plays})
  })

  on(actions.updateSession, (state, session) => {
    return copy(state, {session})
  })
})

sessionStore.subscribe(actions.updateSession)