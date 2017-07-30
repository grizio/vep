import preact from "preact"
import PeriodAdhesionForm from "./periodAdhesionForm";

export interface PeriodAdhesionCreationProps {
  path: string
}
export function PeriodAdhesionCreation(props: PeriodAdhesionCreationProps) {
  return (
    <PeriodAdhesionForm />
  )
}