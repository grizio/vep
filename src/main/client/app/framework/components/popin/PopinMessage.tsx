import preact from "preact"
import {Function0} from "../../lib";
import {Card, CardAction, CardContent} from "../card/Card";

type PopinMessageType = "error" | "success" | ""

export default function popinMessage(title: string, message: string, type: PopinMessageType = "") {
  const wrapper = document.createElement("div")
  document.body.appendChild(wrapper)
  const clean = () => {
    document.body.removeChild(wrapper)
  }
  preact.render(
    <PopinMessage
      title={title}
      message={message}
      type={type}
      onConfirm={clean}
    />,
    wrapper
  );
}

interface PopinMessageProps {
  title: string
  message: string
  type: PopinMessageType
  onConfirm: Function0<void>
}

class PopinMessage extends preact.Component<PopinMessageProps, any> {
  render(props: PopinMessageProps) {
    return (
      <div class="overlay">
        <Card title={props.title} className={`popin-message ${props.type}`}>
          <CardContent>
            <p>{props.message}</p>
          </CardContent>
          <CardAction className="primary" action={this.props.onConfirm}>Ok</CardAction>
        </Card>
      </div>
    )
  }
}