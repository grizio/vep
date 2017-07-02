import preact from "preact";

import Header from "./header/index";
import Home from "../routes/home";
import Profile from "../routes/profile";
import {Router} from "preact-router/src";

interface AppState {
  url: string
}

export default class App extends preact.Component<any, AppState> {
  handleRoute = (e: any) => {
    this.setState({url: e.url})
  };

  render() {
    return (
      <div id="app">
        <Header />
        <Router onChange={this.handleRoute}>
          <Home path="/"/>
          <Profile path="/profile/" user="me"/>
          <Profile path="/profile/:user"/>
        </Router>
      </div>
    );
  }
}
