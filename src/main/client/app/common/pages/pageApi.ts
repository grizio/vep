import {request} from "../../framework/utils/http";
import {PageInformation} from "./pageModel";

export function findAllPages(): Promise<Array<PageInformation>> {
  return request({
    method: "GET",
    url: `pages`
  })
}

export function createPage(page: PageInformation) {
  return request({
    method: "POST",
    url: `pages/${page.canonical}`,
    entity: page
  })
}

export function updatePage(page: PageInformation) {
  return Promise.reject(null)
}