import preact from "preact"
import CompanyForm from "./companyForm";

export interface CompanyCreationProps {
  path: string
}

export function CompanyCreation(props: CompanyCreationProps) {
  return (
    <CompanyForm />
  )
}