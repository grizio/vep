import preact from "preact"
import * as preactRouter from "preact-router/src"
import {forceStartWith} from "../utils/strings"
import {Function0} from "../lib";
import classnames from "classnames";
import {OnGranted} from "./Security";

const PreactLink = preactRouter.Link

export interface ButtonComponentProps {
  type?: "submit" | "reset"
  message: string
  action?: Function0<void>
  disabled?: boolean
  className?: string
}

interface InternalButtonComponentProps extends ButtonComponentProps {
  class?: string
}

export interface LinkComponentProps {
  href: string
  target?: "_blank" | "blank" | "_self" | "self" | "_parent" | "parent" | "_top" | "top"
  message: string
  disabled?: boolean
  className?: string
}

interface InternalLinkComponentProps extends LinkComponentProps {
  class?: string
}

type ButtonProps = ButtonComponentProps | LinkComponentProps

export function PrimaryButton(props: ButtonProps) {
  return renderButtonOrLink(props, "primary")
}

export const AdminPrimaryButton = OnGranted<ButtonProps>(
  (props: ButtonProps) => <PrimaryButton {...props} />,
  "admin"
)

export function SecondaryButton(props: ButtonProps) {
  return renderButtonOrLink(props, "secondary")
}

export function FlatPrimaryButton(props: ButtonProps) {
  return renderButtonOrLink(props, "flat primary")
}

export function FlatSecondaryButton(props: ButtonProps) {
  return renderButtonOrLink(props, "flat secondary")
}

export function DeleteButton(props: ButtonProps) {
  return renderButtonOrLink(props, "delete")
}

export function IconDeleteButton(props: ButtonProps) {
  return renderButtonOrLink(props, "icon delete")
}

export function ActionButton(props: ButtonProps) {
  return renderButtonOrLink(props, "action")
}

function renderButtonOrLink(props: ButtonProps, className: string) {
  if ((props as LinkComponentProps).href) {
    const link = props as LinkComponentProps
    return (
      <Link
        href={link.href}
        target={link.target}
        class={classnames(props.className, className)}
        message={props.message}
        disabled={props.disabled}
      />
    )
  } else {
    const button = props as ButtonComponentProps
    return (
      <Button
        type={button.type}
        class={classnames(props.className, className)}
        message={button.message}
        action={button.action}
        disabled={button.disabled}
      />
    )
  }
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
  if (props.href.startsWith("http") || props.target && props.target !== "self" && props.target !== "_self") {
    return (
      <a
        href={props.href}
        target={props.target ? forceStartWith(props.target, "_") : null}
        class={`button ${props.class}`}
        disabled={props.disabled}
      >{props.message}</a>
    )
  } else {
    return (
      <PreactLink
        href={props.href}
        target={props.target ? forceStartWith(props.target, "_") : null}
        class={`button ${props.class}`}
        disabled={props.disabled}
      >{props.message}</PreactLink>
    )
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