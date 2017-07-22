import {request} from "../../framework/utils/http";
import {Company, CompanyCreation} from "./companyModel";

export function findAllCompanies(): Promise<Array<Company>> {
  return request({
    method: "GET",
    url: "production/companies"
  })
}

export function findCompany(id: string): Promise<Company> {
  return request({
    method: "GET",
    url: `production/companies/${id}`
  })
}

export function createCompany(company: CompanyCreation) {
  return request({
    method: "POST",
    url: "production/companies",
    entity: company
  })
}

export function updateCompany(company: Company) {
  return request({
    method: "PUT",
    url: `production/companies/${company.id}`,
    entity: company
  })
}

export function deleteCompany(company: Company) {
  return request({
    method: "DELETE",
    url: `production/companies/${company.id}`,
    entity: company
  })
}