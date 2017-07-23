import preact from "preact"
import PlayForm from "./playForm";

export interface PlayCreationProps {
  path: string
  company?: string
  show?: string
}

export function PlayCreation(props: PlayCreationProps) {
  return (
    <PlayForm company={props.company} show={props.show} />
  )
}