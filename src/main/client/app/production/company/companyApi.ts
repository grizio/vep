import {request} from "../../framework/utils/http";
import {Company, CompanyCreation, Show, ShowCreation} from "./companyModel";

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

export function createShow(company: string, show: ShowCreation) {
  return request({
    method: "POST",
    url: `production/companies/${company}`,
    entity: show
  })
}

export function updateShow(company: string, show: Show) {
  return request({
    method: "PUT",
    url: `production/companies/${company}/${show.id}`,
    entity: show
  })
}