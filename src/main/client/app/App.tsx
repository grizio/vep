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
import PlayList from "./production/play/playList/playList";
import ShowList from "./production/show/showList/showList";
import {PageCreation, PageUpdate} from "./common/pages/pageForm/index";
import PageRead from "./common/pages/pageRead/pageRead";
import ProfileForm from "./user/profile/profileForm/profileForm";
import {PeriodAdhesionCreation, PeriodAdhesionUpdate} from "./user/adhesion/periodAdhesionForm/index";
import PeriodAdhesionList from "./user/adhesion/periodAdhesionList/periodAdhesionList";
import RequestAdhesionForm from "./user/adhesion/requestAdhesion/requestAdhesionForm";
import ProfilePage from "./user/profile/profilePage/profilePage";
import AdhesionList from "./user/adhesion/adhesionList/adhesionList";
import ResetPassword from "./user/session/resetPassword/resetPassword";
import Sitemap from "./common/sitemap/sitemap";

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
        <div class="col col-fix-300">
          <Navigation />
        </div>
        <div class="col-fill">
          <Router onChange={this.handleRoute}>
            <Home path="/"/>
            <Contact path="/contact"/>
            <Sitemap path="/sitemap"/>

            <PageRead path="/page/:canonical"/>
            <PageCreation path="/cms/page/create"/>
            <PageUpdate path="/cms/page/update/:canonical"/>

            <Register path="/personal/register" />
            <Login path="/personal/login" />
            <ResetPassword path="/personal/password/reset" />

            <ProfilePage path="/personal/profile" />
            <ProfilePage path="/personal/profile/read/:id" />
            <ProfileForm path="/personal/profile/update" />
            <RequestAdhesionForm path="/personal/adhesions/register" />

            <TheaterList path="/production/theaters" />
            <TheaterCreation path="/production/theaters/create" />
            <TheaterUpdate path="/production/theaters/update/:id" />

            <CompanyList path="/production/companies" />
            <CompanyPage path="/production/companies/page/:id" />
            <CompanyCreation path="/production/companies/create" />
            <CompanyUpdate path="/production/companies/update/:id" />

            <ShowList path="/production/shows" />
            <ShowPage path="/production/companies/:company/shows/page/:id" />
            <ShowCreation path="/production/companies/:company/shows/create" />
            <ShowUpdate path="/production/companies/:company/shows/update/:id" />

            <PlayList path="/production/plays" />
            <PlayCreation path="/production/companies/:company/shows/:show/plays/create" />
            <PlayUpdate path="/production/companies/:company/shows/:show/plays/update/:id" />
            <PlayPage path="/production/companies/:company/shows/:show/plays/page/:id" />

            <PeriodAdhesionList path="/adhesions" />
            <PeriodAdhesionCreation path="/adhesions/create" />
            <PeriodAdhesionUpdate path="/adhesions/update/:id" />

            <AdhesionList path="/adhesions/list/:period" />
          </Router>
        </div>
      </div>
    )
  }
}
