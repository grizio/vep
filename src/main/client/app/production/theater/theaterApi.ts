import {TheaterCreation} from "./theaterModel";
import {request} from "../../framework/utils/http";

export function createTheater(theater: TheaterCreation) {
  return request({
    method: "POST",
    url: "production/theaters",
    entity: theater
  })
}