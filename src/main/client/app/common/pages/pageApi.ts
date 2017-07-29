import {request} from "../../framework/utils/http";
import {PageInformation} from "./pageModel";

export function findAllPages(): Promise<Array<PageInformation>> {
  return request({
    method: "GET",
    url: `pages`
  })
}

export function findPage(canonical: string): Promise<PageInformation> {
  return request({
    method: "GET",
    url: `pages/${canonical}`
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
  return request({
    method: "PUT",
    url: `pages/${page.canonical}`,
    entity: page
  })
}