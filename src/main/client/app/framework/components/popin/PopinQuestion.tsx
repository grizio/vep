import preact from "preact"
import {Function0, Function1} from "../../lib";
import {Card, CardAction, CardContent} from "../card/Card";
import Input from "../form/Input";

export default function popinQuestion(title: string, message: string): Promise<string> {
  return new Promise((resolve, reject) => {
    const wrapper = document.createElement("div")
    document.body.appendChild(wrapper)
    const clean = () => {
      document.body.removeChild(wrapper)
    }
    const onConfirm = (answer: string) => {
      clean()
      resolve(answer)
    }
    const onCancel = () => {
      clean()
      reject()
    }
    preact.render(
      <PopinQuestion
        title={title}
        message={message}
        onConfirm={onConfirm}
        onCancel={onCancel}
      />,
      wrapper
    );
  })
}

interface PopinQuestionProps {
  title: string
  message: string
  onConfirm: Function1<string, void>
  onCancel: Function0<void>
}

interface PopinQuestionState {
  answer: string
}

class PopinQuestion extends preact.Component<PopinQuestionProps, PopinQuestionState> {
  render(props: PopinQuestionProps, state: PopinQuestionState) {
    return (
      <div class="overlay">
        <Card title={props.title} className="popin-question">
          <CardContent>
            <form onSubmit={this.confirm}>
              <Input
                id="popin-question-answer"
                label={props.message}
                name="popin-question-answer"
                type="textarea"
                placeholder=" "
                onUpdate={(value) => this.updateAnswer(value)}
                fieldValidation={{
                  value: state.answer,
                  errors: [],
                  changed: false
                }}
              />
            </form>
          </CardContent>
          <CardAction className="primary" action={this.confirm}>Confirmer</CardAction>
          <CardAction action={this.cancel}>Annuler</CardAction>
        </Card>
      </div>
    )
  }

  updateAnswer = (answer: string) => {
    this.setState({answer})
  }

  confirm = () => {
    this.props.onConfirm(this.state.answer)
  }

  cancel = () => {
    this.props.onCancel()
  }
}