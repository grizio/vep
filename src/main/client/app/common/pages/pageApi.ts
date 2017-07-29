import {request} from "../../framework/utils/http";
import {PageInformation} from "./pageModel";

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