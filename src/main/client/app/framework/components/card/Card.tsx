import preact from "preact";
import {Function0} from "../../lib";
import {ActionButton} from "../buttons";
import {Link} from "preact-router/src";

interface CardProps {
  title?: string
  image?: string
  href?: string
  className?: string
  children?: Array<preact.VNode>
}

interface CardContentProps {
  children?: Array<preact.VNode>
}

interface CardActionProps {
  href?: string
  target?: "_blank" | "blank" | "_self" | "self" | "_parent" | "parent" | "_top" | "top"
  className?: string
  action?: Function0<void>
  children?: Array<preact.VNode>
}

export function Card(props: CardProps) {
  return (
    <div class={`card ${props.className || ""}`}>
      {renderImage(props)}
      {
        props.href
          ? (<Link href={props.href} class="card-link">{renderText(props)}</Link>)
          : (<div>{renderText(props)}</div>)
      }
      {renderActions(props)}
    </div>
  )
}

export function CardContent(props: CardContentProps) {
  return (
    <div class="card-content">
      {props.children}
    </div>
  )
}

export function CardAction(props: CardActionProps) {
  if (props.action) {
    return (
      <ActionButton message={props.children.toString()} action={props.action} className={props.className}  />
    )
  } else {
    return (
      <ActionButton message={props.children.toString()} href={props.href} target={props.target} className={props.className} />
    )
  }
}

function renderImage(props: CardProps) {
  if (props.image) {
    return (
      <div class="card-image">
        <img src={props.image} alt={props.title}/>
      </div>
    )
  } else {
    return null
  }
}

function renderText(props: CardProps) {
  return (
    <div class="card-text">
      {renderTitle(props)}
      {renderContent(props)}
    </div>
  )
}

function renderTitle(props: CardProps) {
  if (props.title) {
    return (
      <div class="card-title">{props.title}</div>
    )
  } else {
    return null
  }
}

function renderContent(props: CardProps) {
  return props.children.find(_ => _.nodeName === (CardContent as any))
}

function renderActions(props: CardProps) {
  const actionVNodes = props.children.filter(_ => _.nodeName === (CardAction as any))
  if (actionVNodes && actionVNodes.length > 0) {
    return (
      <div class="card-actions">
        {actionVNodes}
      </div>
    )
  } else {
    return null
  }
}