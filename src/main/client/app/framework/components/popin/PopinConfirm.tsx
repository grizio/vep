import preact from "preact"
import {Function0} from "../../lib";
import {Card, CardAction, CardContent} from "../card/Card";

export default function popinConfirm(title: string, message: string): Promise<string> {
  return new Promise((resolve, reject) => {
    const wrapper = document.createElement("div")
    document.body.appendChild(wrapper)
    const clean = () => {
      document.body.removeChild(wrapper)
    }
    const onConfirm = () => {
      clean()
      resolve()
    }
    const onCancel = () => {
      clean()
      reject()
    }
    preact.render(
      <PopinConfirm
        title={title}
        message={message}
        onConfirm={onConfirm}
        onCancel={onCancel}
      />,
      wrapper
    );
  })
}

interface PopinConfirmProps {
  title: string
  message: string
  onConfirm: Function0<void>
  onCancel: Function0<void>
}

class PopinConfirm extends preact.Component<PopinConfirmProps, any> {
  render(props: PopinConfirmProps) {
    return (
      <div class="overlay">
        <Card title={props.title} className="popin-confirm">
          <CardContent>
            <p>{props.message}</p>
          </CardContent>
          <CardAction className="primary" action={this.confirm}>Confirmer</CardAction>
          <CardAction action={this.cancel}>Annuler</CardAction>
        </Card>
      </div>
    )
  }

  confirm = () => {
    this.props.onConfirm()
  }

  cancel = () => {
    this.props.onCancel()
  }
}