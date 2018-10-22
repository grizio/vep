import preact from "preact"
import {Link} from "preact-router/src";
import {AsyncPage} from "../../../framework/components/Page"
import CardCollection from "../../../framework/components/card/CardCollection";
import {ShowListState, showListStore} from "./showListStore";
import {ShowWithDependencies} from "../showModel";
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import {isAfterNow, longDateTimeFormat} from "../../../framework/utils/dates";
import {RichContent} from "../../../framework/components/RichContent";
import messages from "../../../framework/messages";
import {findShowsWithDependencies} from "../showApi";
import * as actions from "./showListActions"

export interface ShowListProps {
  path: string
}

export default class ShowList extends AsyncPage<ShowListProps, ShowListState> {
  constructor() {
    super(showListStore)
  }

  initialize() {
    return findShowsWithDependencies().then(actions.updateList)
  }

  getTitle() {
    return "Liste des prochains spectacles"
  }

  getLoadingMessage() {
    return messages.production.show.list.loading
  }

  renderPage(props: ShowListProps, state: ShowListState) {
    return (
      <div>
        {this.renderShows(state.nextShows, "Prochains spectacles")}
        {this.renderShows(state.previousShows, "Précédents spectacles")}
      </div>
    )
  }

  renderShows(shows: Array<ShowWithDependencies>, title: string) {
    if (shows && shows.length) {
      return (
        <div>
          <h2>{title}</h2>
          <CardCollection columns={2}>
            {shows.map(show => this.renderShow(show))}
          </CardCollection>
        </div>
      )
    } else {
      return null
    }
  }

  renderShow(show: ShowWithDependencies) {
    return (
      <Card title={show.show.title}>
        <CardContent>
          <p>Par {show.company.name}</p>
          <p>Écrit par {show.show.author}</p>
          <p>Mise en scène par {show.show.director}</p>
          <RichContent content={show.show.content} limit={100}/>
          {this.renderPlays(show)}
        </CardContent>
        <CardAction href={`/production/companies/${show.company.id}/shows/page/${show.show.id}`}>
          Plus d'information
        </CardAction>
      </Card>
    )
  }

  renderPlays(show: ShowWithDependencies) {
    if (show.plays && show.plays.length) {
      return (
        <ul>
          {show.plays.filter(_ => isAfterNow(_.date)).map(play =>
            <li>
              <Link href={`/production/companies/${show.company.id}/shows/${show.show.id}/plays/page/${play.id}`}>
                {longDateTimeFormat(play.date)}, {play.theater.name}
              </Link>
            </li>
          )}
        </ul>
      )
    } else {
      return null
    }
  }
}