import preact from "preact"

import {Router} from "preact-router/src"
import Navigation from "./Navigation"
import Home from "./common/home/home"

interface AppState {
  url: string
}

export default class App extends preact.Component<any, AppState> {
  handleRoute = (e: any) => {
    this.setState({url: e.url})
  }

  render() {
    return (
      <div id="app" class="row no-separator">
        <div class="col col-fix-450">
          <Navigation />
        </div>
        <div class="col-fill">
          <Router onChange={this.handleRoute}>
            <Home path="/"/>
          </Router>
        </div>
      </div>
    )
  }
}
