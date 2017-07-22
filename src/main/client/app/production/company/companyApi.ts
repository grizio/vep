import {request} from "../../framework/utils/http";
import {CompanyCreation} from "./companyModel";

export function createCompany(company: CompanyCreation) {
  return request({
    method: "POST",
    url: "production/companies",
    entity: company
  })
}