import preact from "preact"
import {Link} from "preact-router/src"
import classnames from "classnames"
import StoreListenerComponent from "../framework/utils/dom"
import {logout} from "../framework/session/sessionActions"
import {NavigationState, navigationStore} from "./NavigationStore"
import {findNextPlays, findNextShows} from "../production/company/companyApi"
import * as actions from "./NavigationActions"
import {shortDateFormat} from "../framework/utils/dates";
import * as arrays from "../framework/utils/arrays";

interface MenuGroupProps {
  name: string
  href: string
  regex?: string
}

interface MenuItemProps {
  name: string
  href?: string
  action?: Function
  regex?: string
  disabled?: string
}

export default class Navigation extends StoreListenerComponent<any, NavigationState> {
  constructor() {
    super(navigationStore())
  }

  componentDidMount() {
    super.componentDidMount()
    Promise.all([
      findNextShows(),
      findNextPlays()
    ]).then(([shows, plays]) => actions.initialize({shows, plays}))
  }

  render(props: any, state: NavigationState) {
    return (
      <div class="left-navigation">
        {renderSitename()}
        {renderNav(state)}
      </div>
    )
  }
}

function renderSitename() {
  return (
    <div class="sitename">
      <Link href="/" class="row middle center">
        <img src="/assets/logo-acve.png" alt="Logo de Voir & entendre" class="col"/>
        <span class="col">Voir &amp; entendre</span>
      </Link>
    </div>
  )
}

function renderNav(state: NavigationState) {
  return (
    <nav>
      <MenuGroup name="Accueil" href="/" />
      {renderNextShows(state)}
      {renderNextPlays(state)}
      <MenuGroup name="L'association" href="/page/l'association">
        <MenuItem name="Le bureau" href="/page/bureau" />
        <MenuItem name="Historique" href="/page/historique" />
        <MenuItem name="Scéne et Loire" href="/page/scene-et-loire" />
        <MenuItem name="La compagnie du coin" href="/page/la-compagnie-du-coin" />
        <MenuItem name="Ateliers théâtre" href="/page/les-ateliers" />
        <MenuItem name="Ateliers chant" href="/page/atelier-chant" />
      </MenuGroup>
      {
        isGranted(state, "admin") &&
        <MenuGroup name="Les théâtres" href="/production/theaters" regex="/production/theaters(/.*)?">
          <MenuItem name="Tous les théâtres" href="/production/theaters" />
          <MenuItem name="Créer un nouveau théâtre" href="/production/theaters/create" />
        </MenuGroup>
      }
      {
        isGranted(state, "admin") &&
        <MenuGroup name="Les troupes" href="/production/companies" regex="/production/companies(/.*)?">
          <MenuItem name="Toutes les troupes" href="/production/companies" />
          <MenuItem name="Créer une nouvelle troupe" href="/production/companies/create" />
        </MenuGroup>
      }
      {
        isNotLoggedIn(state) &&
        <MenuGroup name="Mon espace" href="/personal/login" regex="/personal(/.*)?">
          <MenuItem name="Se connecter" href="/personal/login" />
          <MenuItem name="S'inscrire" href="/personal/register" />
        </MenuGroup>
      }
      {
        isLoggedIn(state) &&
        <MenuGroup name={`Mon espace (${state.session.user.email})`} href="/personal/login" regex="/personal(/.*)?">
          <MenuItem name="Ma fiche" href="/personal/my-card" />
          <MenuItem name="(Ré)inscription aux activités" href="/personal/register" />
          <MenuItem name="Déconnexion" action={logout} />
        </MenuGroup>
      }
      <MenuGroup name="Nous contacter" href="/contact" />
    </nav>
  )
}

function renderNextShows(state: NavigationState) {
  if (state.shows && state.shows.length) {
    return (
      <MenuGroup name="Les prochaines pièces" href="/shows" regex="/show(s|/.*)">
        {arrays.take(state.shows, 5).map(show =>
          <MenuItem name={show.title} href={`/production/companies/${show.company}/shows/page/${show.id}`}/>
        )}
      </MenuGroup>
    )
  } else {
    return <MenuGroup name="Les prochaines pièces" href="/shows" regex="/show(s|/.*)"/>
  }
}

function renderNextPlays(state: NavigationState) {
  if (state.plays && state.plays.length) {
    return (
      <MenuGroup name="Les prochaines séances" href="/plays" regex="/play(s|/.*)">
        {arrays.take(state.plays, 5).map(play =>
          <MenuItem
            name={`${shortDateFormat(play.date)} • ${play.show}`}
            href={`/production/companies/${play.company}/shows/${play.showId}/plays/page/${play.id}`}
          />
        )}
      </MenuGroup>
    )
  } else {
    return <MenuGroup name="Les prochaines séances" href="/plays" regex="/play(s|/.*)"/>
  }
}

function isLoggedIn(state: NavigationState) {
  return (state.session && state.session.user)
}

function isNotLoggedIn(state: NavigationState) {
  return !isLoggedIn(state)
}

function isGranted(state: NavigationState, role: string) {
  return isLoggedIn(state) && state.session.user.role === role
}

function MenuGroup(props: MenuGroupProps) {
  return (
    <div class={classnames("menu-group", {"active": isElementActive(props)})}>
      <Link href={props.href}>{props.name}</Link>
      <div class="menu-items">
        {props["children"]}
      </div>
    </div>
  )
}

function MenuItem(props: MenuItemProps) {
  if (props.disabled) {
    return (
      <div class={classnames("menu-item", "disabled")}>
        <span title={props.disabled}>
          {props.name}
        </span>
      </div>
    )
  } else if (props.action) {
    return (
      <div class={classnames("menu-item", {"active": isElementActive(props)})}>
        <Link href="#" onClick={props.action}>
          {props.name}
        </Link>
      </div>
    )
  } else {
    return (
      <div class={classnames("menu-item", {"active": isElementActive(props)})}>
        <Link href={props.href}>
          {props.name}
        </Link>
      </div>
    )
  }
}

function isElementActive(props: MenuGroupProps | MenuItemProps) {
  const regex = props.regex || props.href
  return regex && new RegExp(`^${regex}$`).test(location.pathname)
}