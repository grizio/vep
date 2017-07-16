import preact from "preact"
import {forceStartWith} from "../utils/strings"
import {Function0} from "../lib";

export interface ButtonComponentProps {
  type?: "submit" | "reset"
  message: string
  action?: Function0<void>
  disabled?: boolean
}

interface InternalButtonComponentProps extends ButtonComponentProps {
  class?: string
}

export interface LinkComponentProps {
  href: string
  target?: "_blank" | "blank" | "_self" | "self" | "_parent" | "parent" | "_top" | "top"
  message: string
  disabled?: boolean
}

interface InternalLinkComponentProps extends LinkComponentProps {
  class?: string
}

type ButtonProps = ButtonComponentProps | LinkComponentProps

export function PrimaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="primary" message={props.message} action={props.action} disabled={props.disabled} />
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="primary" message={props.message} disabled={props.disabled}/>
    )
  })
}

export function SecondaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="secondary" message={props.message} action={props.action} disabled={props.disabled}/>
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="secondary" message={props.message} disabled={props.disabled}/>
    )
  })
}

export function FlatPrimaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="flat primary" message={props.message} action={props.action} disabled={props.disabled}/>
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="flat primary" message={props.message} disabled={props.disabled}/>
    )
  })
}

export function FlatSecondaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="flat secondary" message={props.message} action={props.action} disabled={props.disabled}/>
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="flat secondary" message={props.message} disabled={props.disabled}/>
    )
  })
}

function Button(props: InternalButtonComponentProps) {
  return (
    <button
      type={props.type}
      class={`button ${props.class}`}
      onClick={onButtonClick(props.action)}
      disabled={props.disabled}
    >{props.message}</button>
  )
}

function Link(props: InternalLinkComponentProps) {
  return (
    <a
      href={props.href}
      target={props.target ? forceStartWith(props.target, "_") : null}
      class={`button ${props.class}`}
      disabled={props.disabled}
    >{props.message}</a>
  )
}

interface ButtonOrLinkParameters {
  onLink(props: LinkComponentProps): JSX.Element
  onButton(props: ButtonComponentProps): JSX.Element
}
function buttonOrLink(props: ButtonProps, parameters: ButtonOrLinkParameters) {
  if ((props as LinkComponentProps).href) {
    return parameters.onLink(props as LinkComponentProps)
  } else {
    return parameters.onButton(props as ButtonComponentProps)
  }
}

const noAction = () => {}
function onButtonClick(action?: Function0<void>) {
  if (action) {
    return (event: Event) => {
      event.preventDefault()
      action()
    }
  } else {
    return noAction
  }
}