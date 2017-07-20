import preact from "preact"
import TheaterForm from "./theaterForm";

export interface TheaterCreationProps {
  path: string
}

export interface TheaterUpdateProps {
  id?: string
  path: string
}

export function TheaterCreation(props: TheaterCreationProps) {
  return (
    <TheaterForm />
  )
}

export function TheaterUpdate(props: TheaterUpdateProps) {
  return (
    <TheaterForm id={props.id} />
  )
}