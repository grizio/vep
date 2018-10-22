import preact from "preact"
import {AsyncPage} from "../../../framework/components/Page"
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import CardCollection from "../../../framework/components/card/CardCollection";
import * as actions from "./playListActions";
import {PlayListState, playListStore} from "./playListStore";
import {PlayWithDependencies} from "../playModel";
import {findNextPlaysWithDependencies} from "../playApi";
import {longDateTimeFormat} from "../../../framework/utils/dates";
import {RichContent} from "../../../framework/components/RichContent";

export interface PlayListProps {
  path: string
}

export default class PlayList extends AsyncPage<PlayListProps, PlayListState> {
  constructor() {
    super(playListStore)
  }

  initialize() {
    return findNextPlaysWithDependencies().then(actions.updateList)
  }

  getTitle() {
    return "Liste des prochains spectacles"
  }

  getLoadingMessage() {
    return "messages.production.show.list.loading"
  }

  renderPage(props: PlayListProps, state: PlayListState) {
    return (
      <CardCollection columns={2}>
        {state.plays.map(play => this.renderPlay(play))}
      </CardCollection>
    )
  }

  renderPlay(play: PlayWithDependencies) {
    return (
      <Card title={longDateTimeFormat(play.play.date)}>
        <CardContent>
          <p>{play.show.title} par {play.company.name}</p>
          <p>📍 {play.theater.name} ({play.theater.address})</p>
          <RichContent content={play.show.content} limit={100}/>
        </CardContent>
        <CardAction href={`/production/companies/${play.company.id}/shows/${play.show.id}/plays/page/${play.play.id}`}>Réserver</CardAction>
        <CardAction href={`/production/companies/${play.company.id}/shows/page/${play.show.id}`}>Voir le spectacle</CardAction>
      </Card>
    )
  }
}