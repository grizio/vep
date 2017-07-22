import preact from "preact"
import ShowForm from "./showForm";

export interface ShowCreationProps {
  path: string
  company?: string
}

export function ShowCreation(props: ShowCreationProps) {
  return (
    <ShowForm company={props.company} />
  )
}