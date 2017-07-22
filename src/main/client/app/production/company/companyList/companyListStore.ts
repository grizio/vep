import {LocalStore} from "fluxx"
import * as actions from "./companyListActions"
import {copy} from "../../../framework/utils/object";
import * as arrays from "../../../framework/utils/arrays";
import {Company} from "../companyModel";

export interface CompanyListState {
  vepCompanies: Array<Company>
  notVepCompanies: Array<Company>
}

const initialState: CompanyListState = {
  vepCompanies: null,
  notVepCompanies: null
}

export const companyListStore = () => LocalStore(initialState, on => {
  on(actions.updateList, (state, companies) => {
    return copy(state, {
      vepCompanies: arrays.sort(companies.filter(_ => _.isVep), compareCompanies),
      notVepCompanies: arrays.sort(companies.filter(_ => !_.isVep), compareCompanies)
    })
  })
})

function compareCompanies(company1: Company, company2: Company): number {
  return company1.name.localeCompare(company2.name)
}