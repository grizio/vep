import {request} from "../../framework/utils/http";
import {Company, CompanyCreation, PlayCreation, Show, ShowCreation} from "./companyModel";
import {copy} from "../../framework/utils/object";
import {localIsoFormat} from "../../framework/utils/dates";

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

export function findShowsByCompany(company: string): Promise<Array<Show>> {
  return request({
    method: "GET",
    url: `production/companies/${company}/shows`
  })
}

export function findShow(company: string, id: string): Promise<Show> {
  return request({
    method: "GET",
    url: `production/companies/${company}/shows/${id}`
  })
}

export function createShow(company: string, show: ShowCreation) {
  return request({
    method: "POST",
    url: `production/companies/${company}/shows`,
    entity: show
  })
}

export function updateShow(company: string, show: Show) {
  return request({
    method: "PUT",
    url: `production/companies/${company}/shows/${show.id}`,
    entity: show
  })
}

export function deleteShow(company: string, show: Show) {
  return request({
    method: "DELETE",
    url: `production/companies/${company}/shows/${show.id}`
  })
}

export function createPlay(company: string, show: string, play: PlayCreation) {
  return request({
    method: "POST",
    url: `production/companies/${company}/shows/${show}/plays`,
    entity: copy(play, {
      date: localIsoFormat(play.date),
      reservationEndDate: localIsoFormat(play.reservationEndDate)
    } as any)
  })
}