import {request} from "../../framework/utils/http"
import {UserRegistration} from "./registrationModel"

export function register(userRegistration: UserRegistration) {
  return request({
    method: "POST",
    url: "/user/register",
    entity: userRegistration
  })
}