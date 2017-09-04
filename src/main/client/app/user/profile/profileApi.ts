import {request} from "../../framework/utils/http";
import {Profile} from "./profileModel";

export function getCurrentProfile(): Promise<Profile> {
  return request({
    method: "GET",
    url: "user/profile"
  })
}

export function getSpecificProfile(id: string): Promise<Profile> {
  return request({
    method: "GET",
    url: `user/profile/${id}`
  })
}

export function updateProfile(profile: Profile) {
  return request({
    method: "PUT",
    url: "user/profile",
    entity: profile
  })
}