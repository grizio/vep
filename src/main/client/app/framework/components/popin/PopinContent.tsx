import preact from "preact"
import {Function0} from "../../lib";
import {Card, CardAction, CardContent} from "../card/Card";

export default function popinContent(title: string, content: JSX.Element) {
  const wrapper = document.createElement("div")
  document.body.appendChild(wrapper)
  const clean = () => {
    document.body.removeChild(wrapper)
  }
  preact.render(
    <PopinContent
      title={title}
      onConfirm={clean}
    >{content}</PopinContent>,
    wrapper
  );
}

interface PopinMessageProps {
  title: string
  children?: Array<preact.VNode>
  onConfirm: Function0<void>
}

class PopinContent extends preact.Component<PopinMessageProps, any> {
  render(props: PopinMessageProps) {
    return (
      <div class="overlay">
        <Card title={props.title} className="popin-content">
          <CardContent>
            {props.children}
          </CardContent>
          <CardAction className="primary" action={this.props.onConfirm}>Ok</CardAction>
        </Card>
      </div>
    )
  }
}