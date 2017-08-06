import preact from "preact"
import BlogForm from "./blogForm";

export interface BlogCreationProps {
  path: string
}

export interface BlogUpdateProps {
  id?: string
  path: string
}

export function BlogCreation(props: BlogCreationProps) {
  return (
    <BlogForm/>
  )
}

export function BlogUpdate(props: BlogUpdateProps) {
  return (
    <BlogForm id={props.id}/>
  )
}