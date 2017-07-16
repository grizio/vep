export interface TheaterCreation {
  name: string
  address: string
  content: string
  seats: Array<Seat>
}

export interface Seat {
  c: string
  x: number
  y: number
  w: number
  h: number
  t: SeatType
}

export type SeatType = "normal" | "strap" | "stage"