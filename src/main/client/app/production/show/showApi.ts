import {request} from "../../framework/utils/http";
import {Show, ShowCreation, ShowMeta, ShowWithDependencies} from "./showModel";
import {copy} from "../../framework/utils/object";
import {jsonToPlay} from "../play/playApi";

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

export function findShowsWithDependencies(): Promise<Array<ShowWithDependencies>> {
  return request<Array<ShowWithDependencies>>({
    method: "GET",
    url: `production/shows/full`
  }).then(_ => _.map(show => copy(show, {
    plays: show.plays.map(jsonToPlay)
  })))
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