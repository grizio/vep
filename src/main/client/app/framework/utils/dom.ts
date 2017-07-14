import preact from "preact"
import {GlobalStore, LocalStore} from "fluxx"

abstract class StoreListenerComponent<P, S> extends preact.Component<P, S> {
  private unsubscribe: () => void
  private store: LocalStore<S> | GlobalStore<S>
  protected mounted: boolean

  constructor(store: LocalStore<S> | GlobalStore<S>) {
    super()
    this.store = store
    this.mounted = false
  }

  componentDidMount() {
    this.unsubscribe = this.store.subscribe(newState => this.setState(newState))
    this.setState(this.store.state)
    this.mounted = true
  }

  componentWillUnmount() {
    this.unsubscribe()
  }
}
export default StoreListenerComponent