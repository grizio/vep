import {LocalStore} from "fluxx"
import * as actions from "./companyPageActions"
import {copy} from "../../../framework/utils/object";
import {Company, Show} from "../companyModel";

export interface CompanyPageState {
  loading: boolean
  company: Company
  shows: Array<Show>
}

const initialState: CompanyPageState = {
  loading: true,
  company: null,
  shows: []
}

export const companyPageStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {company, shows}) => {
    return copy(state, {loading: false, company, shows})
  })
})