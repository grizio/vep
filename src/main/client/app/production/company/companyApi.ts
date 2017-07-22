import {request} from "../../framework/utils/http";
import {Company, CompanyCreation} from "./companyModel";

export function findAllCompanies(): Promise<Array<Company>> {
  return request({
    method: "GET",
    url: "production/companies"
  })
}

export function createCompany(company: CompanyCreation) {
  return request({
    method: "POST",
    url: "production/companies",
    entity: company
  })
}