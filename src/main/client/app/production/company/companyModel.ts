export interface Company {
  id: string
  name: string
  address: string
  isVep: boolean
  content: string
}

export interface CompanyCreation {
  name: string
  address: string
  isVep: boolean
  content: string
}