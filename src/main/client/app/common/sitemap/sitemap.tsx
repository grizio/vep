import preact from "preact"
import {AsyncPage} from "../../framework/components/Page"
import {SitemapState, sitemapStore} from "./sitemapStore"
import {findAllPages} from "../pages/pageApi"
import {findShowsWithDependencies} from "../../production/show/showApi"
import * as actions from "./sitemapActions"
import {ShowWithDependencies} from "../../production/show/showModel";
import {capitalizeFirstLetter} from "../../framework/utils/strings";
import {longDateTimeFormat} from "../../framework/utils/dates";
import {concat} from "../../framework/utils/arrays";

export interface SitemapProps {
  path: string
}

export default class Sitemap extends AsyncPage<SitemapProps, SitemapState> {
  constructor() {
    super(sitemapStore)
  }

  getTitle(props: SitemapProps, state: SitemapState): string {
    return "Carte du site"
  }

  initialize(props: SitemapProps) {
    return Promise.all([
      findAllPages(),
      findShowsWithDependencies()
    ]).then(([pages, shows]) => actions.initialize({pages, shows}))
  }

  renderPage(props: SitemapProps, state: SitemapState) {
    return (
      <div>
        {this.renderInformation(state)}
        {this.renderShows(state)}
      </div>
    )
  }

  renderInformation(state: SitemapState) {
    return (
      <section>
        <h2>Information</h2>
        {this.renderLinks(state.pages.map(page => [`/page/${page.canonical}`, page.title]) as any)}
      </section>
    )
  }

  renderShows(state: SitemapState) {
    return (
      <section>
        <h2>Spectacles</h2>

        {state.shows.map(_ => this.renderShow(_))}
      </section>
    )
  }

  renderShow(show: ShowWithDependencies) {
    const showLink = [
      `/production/companies/${show.company.id}/shows/page/${show.show.id}`,
      show.show.title
    ]
    const playLinks = show.plays.map(play =>
      [
        `/production/companies/${show.company.id}/shows/${show.show.id}/plays/page/${play.id}`,
        `${capitalizeFirstLetter(longDateTimeFormat(play.date))}`
      ]
    )

    return (
      <div>
        <h3>{show.show.title}</h3>
        {this.renderLinks(concat([showLink], playLinks) as any)}
      </div>
    )
  }

  renderLinks(links: Array<[string, string]>) {
    return (
      <ul>
        {links.map(this.renderLink)}
      </ul>
    )
  }

  renderLink(link: [string, string]) {
    return (
      <li><a href={link[0]}>{link[1]}</a></li>
    )
  }
}