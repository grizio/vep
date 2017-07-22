import {Theater, TheaterCreation} from "./theaterModel";
import {request} from "../../framework/utils/http";

export function findAllTheaters(): Promise<Array<Theater>> {
  return request({
    method: "GET",
    url: "production/theaters"
  })
}

export function findTheater(id: string): Promise<Theater> {
  return request({
    method: "GET",
    url: `production/theaters/${id}`
  })
}

export function createTheater(theater: TheaterCreation) {
  return request({
    method: "POST",
    url: "production/theaters",
    entity: theater
  })
}

export function updateTheater(theater: Theater) {
  return request({
    method: "PUT",
    url: `production/theaters/${theater.id}`,
    entity: theater
  })
}

export function deleteTheater(theater: Theater) {
  return request({
    method: "DELETE",
    url: `production/theaters/${theater.id}`
  })
}