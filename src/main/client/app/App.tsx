import preact from "preact"

import {Router} from "preact-router/src"
import Navigation from "./Navigation"
import Home from "./common/home/home"
import Register from "./user/registration/register/register"
import Login from "./user/session/login/login"
import Contact from "./common/contact/contact/contact";
import TheaterCreation from "./production/theater/theaterCreation/theaterCreation";
import TheaterList from "./production/theater/theaterList/theaterList";

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
            <Contact path="/contact"/>

            <Register path="/personal/register" />
            <Login path="/personal/login" />

            <TheaterList path="/production/theaters" />
            <TheaterCreation path="/production/theaters/create" />
          </Router>
        </div>
      </div>
    )
  }
}
