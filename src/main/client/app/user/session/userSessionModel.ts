export interface UserLogin {
  email: string
  password: string
}

export interface UserSessionResponse {
  token: string
  date: Date
}