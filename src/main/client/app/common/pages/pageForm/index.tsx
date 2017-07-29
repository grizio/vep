import preact from "preact"
import PageForm from "./pageForm";

export interface PageCreationProps {
  path: string
}

export interface PageUpdateProps {
  canonical?: string
  path: string
}

export function PageCreation(props: PageCreationProps) {
  return (
    <PageForm />
  )
}

export function PageUpdate(props: PageUpdateProps) {
  return (
    <PageForm canonical={props.canonical} />
  )
}