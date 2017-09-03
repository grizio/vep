import {forceStartWith} from "./strings"
import messages from "../messages"
import {logout} from "../session/sessionActions"
import {sessionStore} from "../session/sessionStore"

type Method = "GET" | "POST" | "PUT" | "DELETE"

interface RequestParameters {
  method: Method
  url: string
  entity?: any,
  headers?: Array<[string, string]>
}

export function request<T>(parameters: RequestParameters): Promise<T> {
  const {method, url, entity} = parameters
  const session = sessionToken()
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()
    xhr.open(method, realUrl(url), true)
    xhr.setRequestHeader("Content-Type", "application/json")
    if (session) {
      xhr.setRequestHeader("Authorization", `Basic ${session}`)
    }
    if (parameters.headers) {
      parameters.headers.forEach(header => xhr.setRequestHeader(header[0], header[1]))
    }

    xhr.onreadystatechange = () => {
      if (xhr.readyState == 4) {
        if (xhr.status == 200) {
          try {
            resolve(JSON.parse(xhr.responseText))
          } catch (e) {
            resolve(xhr.response)
          }
        } else if (xhr.status == 0) {
          console.error("Server seems offline")
          reject([messages.http.serverError])
        } else {
          if (xhr.status == 401 || xhr.status == 403) {
            console.error(`Not enough rights to do the action ${JSON.stringify(parameters)} (status: ${xhr.status})`)
            logout()
            reject([messages.http.notAccess])
          }
          try {
            const jsonErrors = JSON.parse(xhr.responseText)
            if (jsonErrors.errors) {
              reject(jsonErrors.errors)
            } else {
              console.error("There is no error object in http response: ", jsonErrors)
              reject([messages.http.serverError])
            }
          } catch (e) {
            console.error("Not parsed http response: ", xhr.response)
            reject([messages.http.serverError])
          }
        }
      }
    }

    if (entity) {
      xhr.send(JSON.stringify(entity))
    } else {
      xhr.send()
    }
  })
}

function realUrl(url: string): string {
  if (location.host.includes("localhost")) {
    return `http://localhost:9000/api${forceStartWith(url, "/")}`
  } else {
    return `/api${forceStartWith(url, "/")}`
  }
}

function sessionToken(): string {
  const http = sessionStore.state.http
  if (http) {
    return btoa(`${http.email}:${http.token}`)
  } else {
    return null
  }
}