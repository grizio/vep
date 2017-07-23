import preact from "preact"
import PlayForm from "./playForm";

export interface PlayCreationProps {
  path: string
  company?: string
  show?: string
}

export interface PlayUpdateProps {
  path: string
  company?: string
  show?: string
  id?: string
}

export function PlayCreation(props: PlayCreationProps) {
  return (
    <PlayForm company={props.company} show={props.show} />
  )
}

export function PlayUpdate(props: PlayUpdateProps) {
  return (
    <PlayForm company={props.company} show={props.show} id={props.id} />
  )
}