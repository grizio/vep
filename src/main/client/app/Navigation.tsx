import preact from "preact"
import {Link} from "preact-router/src"
import classnames from "classnames"
import {SessionState, sessionStore} from "./framework/session/sessionStore"
import StoreListenerComponent from "./framework/utils/dom"
import {logout} from "./framework/session/sessionActions"

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

export default class Navigation extends StoreListenerComponent<any, SessionState> {
  constructor() {
    super(sessionStore)
  }

  render(props: any, state: SessionState) {
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

function renderNav(state: SessionState) {
  return (
    <nav>
      <MenuGroup name="Accueil" href="/" />
      <MenuGroup name="Les prochaines pièces" href="/shows" regex="/show(s|/.*)">
        <MenuItem name="Les Rustres" href="/show/read/les-rustres" />
        <MenuItem name="Amour Passion et CX Diesel" href="/show/read/amour-passion-et-cx-diesel" />
      </MenuGroup>
      <MenuGroup name="Les prochaines séances" href="/sessions" regex="/session(s|/.*)">
        <MenuItem name="01/01/2017 20:30 - Les Rustres" href="/session/x" />
        <MenuItem name="02/01/2017 15:30 - Amour Passion et CX Diesel" href="/session/x" />
      </MenuGroup>
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
        <MenuGroup name={`Mon espace (${state.user.email})`} href="/personal/login" regex="/personal(/.*)?">
          <MenuItem name="Ma fiche" href="/personal/my-card" />
          <MenuItem name="(Ré)inscription aux activités" href="/personal/register" />
          <MenuItem name="Déconnexion" action={logout} />
        </MenuGroup>
      }
      <MenuGroup name="Nous contacter" href="/contact" />
    </nav>
  )
}

function isLoggedIn(state: SessionState) {
  return !!state.user
}

function isNotLoggedIn(state: SessionState) {
  return !isLoggedIn(state)
}

function isGranted(state: SessionState, role: string) {
  return isLoggedIn(state) && state.user.role === role
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