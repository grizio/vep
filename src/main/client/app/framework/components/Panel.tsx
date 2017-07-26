import preact from "preact"

export type PanelType = "success" | "error" | "info"

interface CloseEvent {
  (): void
}

interface PanelProps {
  type: PanelType
  onClose?: CloseEvent
}

export default function Panel(props: PanelProps) {
  return (
    <div class={`panel ${props.type}`}>
      <div class="panel-content">
        {props["children"]}
      </div>
      {
        props.onClose &&
        <div class="panel-close">
          <span onClick={props.onClose}>x</span>
        </div>
      }
    </div>
  )
}