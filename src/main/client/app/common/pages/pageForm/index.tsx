import preact from "preact"
import PageForm from "./pageForm";

export interface PageCreationProps {
  path: string
}

export function PageCreation(props: PageCreationProps) {
  return (
    <PageForm />
  )
}