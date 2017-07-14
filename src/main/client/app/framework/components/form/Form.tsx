import preact from "preact"
import {FlatSecondaryButton, SecondaryButton, PrimaryButton} from "../buttons"
import Panel from "../Panel"

interface FormEvent {
  (): void
}

interface FormProps {
  submit: string
  onSubmit: FormEvent
  cancel: string
  onCancel: FormEvent | string
  errors?: Array<string>
  closeErrors: FormEvent
}

export default function Form(props: FormProps) {
  return (
    <form onSubmit={onSubmit(props.onSubmit)}>
      {renderErrors(props.errors, props.closeErrors)}
      {props["children"]}
      {renderRequiredInformation()}
      {renderButtons(props)}
    </form>
  )
}

function onSubmit(concreteOnSubmit: FormEvent) {
  return (event: Event) => {
    event.preventDefault()
    concreteOnSubmit()
  }
}

function renderErrors(errors: Array<string>, onClose: FormEvent) {
  if (errors && errors.length) {
    return (
      <Panel type="error" onClose={onClose}>
        {errors.map((error, index) => <p key={index.toString()}>{error}</p>)}
      </Panel>
    )
  } else {
    return null
  }
}

function renderRequiredInformation() {
  return (
    <div class="required-information">
      * Champs obligatoires
    </div>
  )
}

function renderButtons(props: FormProps) {
  return (
    <div class="actions">
      {renderSubmitButton(props)}
      {renderCancelButton(props)}
    </div>
  )
}

function renderSubmitButton(props: FormProps) {
  return (
    <PrimaryButton
      type="submit"
      message={props.submit}
    />
  )
}

function renderCancelButton(props: FormProps) {
  if (typeof props.onCancel == "string") {
    return (
      <SecondaryButton
        href={props.onCancel}
        message={props.cancel}
      />
    )
  } else {
    return (
      <FlatSecondaryButton
        message={props.cancel}
        action={props.onCancel}
      />
    )
  }
}