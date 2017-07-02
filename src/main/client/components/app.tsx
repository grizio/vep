import preact from "preact";

import Home from "../routes/home";
import Profile from "../routes/profile";
import {Router} from "preact-router/src";
import Navigation from "./Navigation";

interface AppState {
  url: string
}

export default class App extends preact.Component<any, AppState> {
  handleRoute = (e: any) => {
    this.setState({url: e.url})
  };

  render() {
    return (
      <div id="app" class="row no-separator">
        <div class="col">
          <Navigation />
        </div>
        <div class="col-1">
          <Router onChange={this.handleRoute}>
            <Home path="/"/>
            <Profile path="/shows" user="me"/>
            <Profile path="/profile/:user"/>
          </Router>
        </div>
      </div>
    );
  }
}
