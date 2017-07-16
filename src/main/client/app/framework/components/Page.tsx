import preact from "preact";
import {sessionStore, UserRole} from "../session/sessionStore";
import Panel from "./Panel";
import {Link} from "preact-router/src";
import {PrimaryButton, SecondaryButton} from "./buttons";

interface PageProps {
  title: string
  role?: UserRole
}

export default function Page(props: PageProps) {
  return (
    <div class="page">
      <header>
        {props.title}
      </header>
      <section>
        {
          canAccess(props)
            ? props["children"]
            : renderErrorAccess()
        }

      </section>
    </div>
  )
}

function renderErrorAccess() {
  return (
    <Panel type="error">
      <p>
        Vous n'avez pas les droits nécessaires pour accéder à cette page.
      </p>
      <p>
        Vous pouvez tenter de <Link href="/personal/login">vous connecter</Link>.
        Si vous êtes déjà connecté, votre compte ne vous permet pas de réaliser cette action.
      </p>
      <p>
        Si vous pensez que l'accès à cette page ne devrait pas vous être bloqué,
        veuillez <Link href="/contact">nous contacter</Link>.
      </p>
      <p>
        <PrimaryButton href="/" message="Revenir à l'accueil" />
        <SecondaryButton href="/personal/login" message="Se connecter" />
      </p>
    </Panel>
  )
}

function canAccess(props: PageProps): boolean {
  switch (props.role) {
    case "user":
      return !!sessionStore.state.user
    case "admin":
      return sessionStore.state.user && sessionStore.state.user.role === "admin"
    default:
      return true
  }
}