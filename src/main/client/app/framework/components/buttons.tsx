import preact from "preact"
import {forceStartWith} from "../utils/strings"

export interface ButtonAction {
  (): void
}

export interface ButtonComponentProps {
  type?: "submit" | "reset"
  message: string
  action?: ButtonAction
}

interface InternalButtonComponentProps extends ButtonComponentProps {
  class?: string
}

export interface LinkComponentProps {
  href: string
  target?: "_blank" | "blank" | "_self" | "self" | "_parent" | "parent" | "_top" | "top"
  message: string
}

interface InternalLinkComponentProps extends LinkComponentProps {
  class?: string
}

type ButtonProps = ButtonComponentProps | LinkComponentProps

export function PrimaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="primary" message={props.message}/>
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="primary" message={props.message}/>
    )
  })
}

export function SecondaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="secondary" message={props.message}/>
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="secondary" message={props.message}/>
    )
  })
}

export function FlatPrimaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="flat primary" message={props.message}/>
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="flat primary" message={props.message}/>
    )
  })
}

export function FlatSecondaryButton(props: ButtonProps) {
  return buttonOrLink(props, {
    onButton: (props) => (
      <Button type={props.type} class="flat secondary" message={props.message}/>
    ),
    onLink: (props) => (
      <Link href={props.href} target={props.target} class="flat secondary" message={props.message}/>
    )
  })
}

function Button(props: InternalButtonComponentProps) {
  return (
    <button
      type={props.type}
      class={`button ${props.class}`}
    >{props.message}</button>
  )
}

function Link(props: InternalLinkComponentProps) {
  return (
    <a
      href={props.href}
      target={props.target ? forceStartWith(props.target, "_") : null}
      class={`button ${props.class}`}
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