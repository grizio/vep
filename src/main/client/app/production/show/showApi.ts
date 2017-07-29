import {request} from "../../framework/utils/http";
import {Show, ShowCreation, ShowMeta} from "./showModel";

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

export function findNextShows(): Promise<Array<ShowMeta>> {
  return request({
    method: "GET",
    url: `production/shows/next`
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