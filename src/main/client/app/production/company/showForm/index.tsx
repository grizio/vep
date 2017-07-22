import preact from "preact"
import ShowForm from "./showForm";

export interface ShowCreationProps {
  path: string
  company?: string
}

export interface ShowUpdateProps {
  path: string
  company?: string
  id?: string
}

export function ShowCreation(props: ShowCreationProps) {
  return (
    <ShowForm company={props.company} />
  )
}

export function ShowUpdate(props: ShowUpdateProps) {
  return (
    <ShowForm company={props.company} id={props.id} />
  )
}