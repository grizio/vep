export interface Reservation {
  id: string
  firstName: string
  lastName: string
  email: string
  city: string
  comment: string
  seats: Array<string>
}

export interface ReservationCreation {
  firstName: string
  lastName: string
  email: string
  city: string
  comment: string
  seats: Array<string>
}