import {request} from "../../framework/utils/http";
import {Profile} from "./profileModel";

export function getCurrentProfile() {
  return request({
    method: "GET",
    url: "user/profile"
  })
}

export function updateProfile(profile: Profile) {
  return request({
    method: "PUT",
    url: "user/profile",
    entity: profile
  })
}