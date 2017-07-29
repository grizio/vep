export interface Profile {
  email: string
  firstName: string
  lastName: string
  address: string
  zipCode: string
  city: string
  phones: Array<Phone>
}

export interface Phone {
  name: string
  number: string
}