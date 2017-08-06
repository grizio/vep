import {GlobalStore} from "fluxx"
import * as actions from "./sessionActions"

const sessionStorageKey = "session"
const sessionUrl = location.host.includes("localhost")
  ? "http://localhost:9000/api/user/session"
  : "/api/user/session";

export interface SessionState {
  http?: SessionHttpState
  user?: SessionUser
}

interface SessionHttpState {
  email: string
  token: string
  date: Date
}

export interface SessionUser {
  email: string
  role: UserRole
}

export type UserRole = "user" | "admin"

const initialState: SessionState = (() => {
  const localSession = localStorage.getItem(sessionStorageKey)
  if (localSession) {
    return {http: JSON.parse(localSession)}
  } else {
    return {}
  }
})()

export const sessionStore = GlobalStore(initialState, on => {
  on(actions.login, (state, value) => {
    localStorage.setItem(sessionStorageKey, JSON.stringify(value))
    getMetaSession(value).then(actions.updateUser)
    return Object.assign({}, state, {http: value})
  })

  on(actions.logout, (state) => {
    localStorage.removeItem(sessionStorageKey)
    return Object.assign({}, state, {http: null, user: null})
  })

  on(actions.updateUser, (state, value) => {
    return Object.assign({}, state, {user: value})
  })
})

if (sessionStore.state.http) {
  getMetaSession(sessionStore.state.http)
    .then(actions.updateUser)
}

// Simplification of http.ts version because of double dependency
function getMetaSession(http: SessionHttpState) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()
    xhr.open("GET", sessionUrl, true)
    xhr.setRequestHeader("Content-Type", "application/json")
    xhr.setRequestHeader("Authorization", `Basic ${btoa(`${http.email}:${http.token}`)}`)

    xhr.onreadystatechange = () => {
      if (xhr.readyState == 4) {
        if (xhr.status == 200) {
          resolve(JSON.parse(xhr.responseText))
        } else if (xhr.status == 401) {
          console.log(`Session terminated`)
          actions.logout()
          reject(null)
        } else {
          console.error(`Server error`, xhr.status, xhr.responseText)
          reject(null)
        }
      }
    }

    xhr.send()
  })
}