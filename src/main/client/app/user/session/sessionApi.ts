import {UserLogin, UserSessionResponse} from "./userSessionModel"
import {request} from "../../framework/utils/http"

export function login(userLogin: UserLogin) {
  return request<UserSessionResponse>({
    method: "POST",
    url: "/user/login",
    entity: userLogin
  })
}