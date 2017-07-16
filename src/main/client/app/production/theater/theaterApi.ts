import {Theater, TheaterCreation} from "./theaterModel";
import {request} from "../../framework/utils/http";

export function findAllTheaters(): Promise<Array<Theater>> {
  return request({
    method: "GET",
    url: "production/theaters"
  })
}

export function createTheater(theater: TheaterCreation) {
  return request({
    method: "POST",
    url: "production/theaters",
    entity: theater
  })
}