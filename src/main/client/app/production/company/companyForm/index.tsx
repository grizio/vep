import preact from "preact"
import CompanyForm from "./companyForm";

export interface CompanyCreationProps {
  path: string
}

export interface CompanyUpdateProps {
  id?: string
  path: string
}

export function CompanyCreation(props: CompanyCreationProps) {
  return (
    <CompanyForm />
  )
}

export function CompanyUpdate(props: CompanyUpdateProps) {
  return (
    <CompanyForm id={props.id} />
  )
}