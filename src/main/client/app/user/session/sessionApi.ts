import {ResetPassword, UserLogin, UserSessionResponse} from "./userSessionModel"
import {request} from "../../framework/utils/http"

export function login(userLogin: UserLogin) {
  return request<UserSessionResponse>({
    method: "POST",
    url: "/user/login",
    entity: userLogin
  })
}

export function requestResetPassword(email: string) {
  return request({
    method: "POST",
    url: "/user/password/requestReset",
    entity: email
  })
}

export function resetPassword(resetPassword: ResetPassword) {
  return request({
    method: "POST",
    url: "/user/password/reset",
    entity: resetPassword
  })
}