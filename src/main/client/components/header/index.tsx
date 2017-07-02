import preact from "preact";
import {Link} from "preact-router/src";

interface NavLinkProps {
  href: string
  regex?: string
  children?: preact.VNode[]
}

export default function Header() {
  return (
    <header class="header">
      <h1>Voir &amp; Entendre</h1>
      <nav>
        <NavLink href="/">Home</NavLink>
        <NavLink href="/profile">Me</NavLink>
        <NavLink href="/profile/john" regex="/profile/.+">John</NavLink>
      </nav>
    </header>
  );
}

function NavLink(props: NavLinkProps) {
  const match = props.regex || props.href
  if (new RegExp(`^${match}$`).test(location.pathname)) {
    return <Link href={props.href} class="active">{props.children}</Link>
  } else {
    return <Link href={props.href}>{props.children}</Link>
  }
}