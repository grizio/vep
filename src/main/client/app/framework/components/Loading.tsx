import preact from "preact"

interface LoadingProps {
  loading: boolean
  message: string
  children?: Array<preact.VNode[]>
}

export default function Loading(props: LoadingProps) {
  if (props.loading) {
    return (
      <div class="loading">
        <p>
          {props.message}
        </p>
      </div>
    )
  } else {
    return <div>{props.children}</div>
  }

}