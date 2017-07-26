import preact from "preact";
import {SessionState, sessionStore, UserRole} from "../session/sessionStore";
import Panel from "./Panel";
import {Link} from "preact-router/src";
import {PrimaryButton, SecondaryButton} from "./buttons";
import StoreListenerComponent from "../utils/dom";
import {GlobalStore, LocalStore} from "fluxx";
import {Function0} from "../lib";
import Loading from "./Loading";
import messages from "../messages";

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

interface InternalAsyncPageState {
  _loading: boolean
}

type AsyncPageState<A> = A & InternalAsyncPageState

export abstract class AsyncPage<P, S> extends preact.Component<P, AsyncPageState<S>>{
  private storeCreation: Function0<LocalStore<S> | GlobalStore<S>>
  private store: LocalStore<S> | GlobalStore<S>
  private unsubscribe: () => void

  constructor(storeCreation: Function0<LocalStore<S> | GlobalStore<S>>) {
    super()
    this.state = {_loading: true} as any
    this.storeCreation = storeCreation
    this.store = storeCreation()
  }

  componentDidMount() {
    this.unsubscribe = this.store.subscribe(newState => this.setState(newState))
    this.setState(this.store.state)
    this.executeInitialize(this.props)
  }

  componentWillUnmount() {
    this.unsubscribe()
  }

  componentWillReceiveProps(nextProps: P) {
    if (this.props["url"] !== nextProps["url"]) {
      this.unsubscribe()
      this.store = this.storeCreation()
      this.unsubscribe = this.store.subscribe(newState => this.setState(newState))
      this.executeInitialize(nextProps)
    }
  }

  render(props: P, state: AsyncPageState<S>) {
    const title = state._loading ? "" : this.getTitle(props, state)
    return (
      <Page title={title} role={this.getRole(props, state)}>
        <Loading loading={state._loading} message={this.getLoadingMessage(props, state)}>
          {!state._loading && this.renderPage(props, state)}
        </Loading>
      </Page>
    )
  }

  private executeInitialize(props: P) {
    this.setState({_loading: true})
    this.initialize(props)
      .then(() => this.setState({_loading: false}))
  }

  abstract initialize(props: P): Promise<any>

  abstract renderPage(props: P, state: S): preact.VNode

  abstract getTitle(props: P, state: S): string

  getRole(props: P, state: S): UserRole {
    return null
  }

  getLoadingMessage(props: P, state: S): string {
    return messages.common.loading
  }
}