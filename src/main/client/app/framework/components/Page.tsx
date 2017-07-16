import preact from "preact";
import {SessionState, sessionStore, UserRole} from "../session/sessionStore";
import Panel from "./Panel";
import {Link} from "preact-router/src";
import {PrimaryButton, SecondaryButton} from "./buttons";
import StoreListenerComponent from "../utils/dom";

interface PageProps {
  title: string
  role?: UserRole
}

export default class Page extends StoreListenerComponent<PageProps, SessionState> {
  constructor() {
    super(sessionStore)
  }

  render(props: PageProps, state: SessionState) {
    return (
      <div class="page">
        <header>
          {props.title}
        </header>
        <section>
          {
            canAccess(props, state)
              ? props["children"]
              : renderErrorAccess()
          }
        </section>
      </div>
    )
  }
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

function canAccess(props: PageProps, state: SessionState): boolean {
  switch (props.role) {
    case "user":
      return !!state.user
    case "admin":
      return state.user && state.user.role === "admin"
    default:
      return true
  }
}