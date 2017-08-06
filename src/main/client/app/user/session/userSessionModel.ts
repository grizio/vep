export interface UserLogin {
  email: string
  password: string
}

export interface UserSessionResponse {
  token: string
  date: Date
}

export interface ResetPassword {
  email: string
  token: string
  password: string
}