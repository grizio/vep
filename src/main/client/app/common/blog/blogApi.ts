import {request} from "../../framework/utils/http";
import {Blog, BlogCreation, BlogUpdate} from "./blogModel";
import {copy} from "../../framework/utils/object";
import {localIsoFormatToDate} from "../../framework/utils/dates";

export function findLastBlog(): Promise<Array<Blog>> {
  return request<Array<Blog>>({
    method: "GET",
    url: `blog/last`
  }).then(_ => _.map(jsonToBlog))
}

export function findBlog(id: string): Promise<Blog> {
  return request<Blog>({
    method: "GET",
    url: `blog/${id}`
  }).then(jsonToBlog)
}

export function createBlog(blog: BlogCreation): Promise<Blog> {
  return request<Blog>({
    method: "POST",
    url: `blog`,
    entity: blog
  }).then(jsonToBlog)
}

export function updateBlog(blog: BlogUpdate): Promise<Blog> {
  return request<Blog>({
    method: "PUT",
    url: `blog/${blog.id}`,
    entity: blog
  }).then(jsonToBlog)
}

function jsonToBlog(blog: Blog): Blog {
  return copy(blog, {
    date: localIsoFormatToDate(blog.date as any)
  })
}