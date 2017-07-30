import preact from "preact"
import PeriodAdhesionForm from "./periodAdhesionForm";

export interface PeriodAdhesionCreationProps {
  path: string
}

export interface PeriodAdhesionUpdateProps {
  id?: string
  path: string
}

export function PeriodAdhesionCreation(props: PeriodAdhesionCreationProps) {
  return (
    <PeriodAdhesionForm />
  )
}

export function PeriodAdhesionUpdate(props: PeriodAdhesionUpdateProps) {
  return (
    <PeriodAdhesionForm id={props.id} />
  )
}