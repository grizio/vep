import preact from "preact"

import {Router} from "preact-router/src"
import Navigation from "./navigation/Navigation"
import Home from "./common/home/home"
import Register from "./user/registration/register/register"
import Login from "./user/session/login/login"
import Contact from "./common/contact/contact/contact";
import TheaterList from "./production/theater/theaterList/theaterList";
import {TheaterCreation, TheaterUpdate} from "./production/theater/theaterForm/index";
import {CompanyCreation, CompanyUpdate} from "./production/company/companyForm/index";
import CompanyList from "./production/company/companyList/companyList";
import CompanyPage from "./production/company/companyPage/companyPage";
import ShowPage from "./production/show/showPage/showPage";
import {ShowCreation, ShowUpdate} from "./production/show/showForm/index";
import {PlayCreation, PlayUpdate} from "./production/play/playForm/index";
import PlayPage from "./production/play/playPage/playPage";

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
            <TheaterUpdate path="/production/theaters/update/:id" />

            <CompanyList path="/production/companies" />
            <CompanyPage path="/production/companies/page/:id" />
            <CompanyCreation path="/production/companies/create" />
            <CompanyUpdate path="/production/companies/update/:id" />

            <ShowPage path="/production/companies/:company/shows/page/:id" />
            <ShowCreation path="/production/companies/:company/shows/create" />
            <ShowUpdate path="/production/companies/:company/shows/update/:id" />

            <PlayCreation path="/production/companies/:company/shows/:show/plays/create" />
            <PlayUpdate path="/production/companies/:company/shows/:show/plays/update/:id" />
            <PlayPage path="/production/companies/:company/shows/:show/plays/page/:id" />
          </Router>
        </div>
      </div>
    )
  }
}
