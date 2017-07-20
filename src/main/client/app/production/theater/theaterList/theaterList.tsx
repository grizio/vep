import preact from "preact"
import Page from "../../../framework/components/Page"
import StoreListenerComponent from "../../../framework/utils/dom"
import {TheaterListState, theaterListStore} from "./theaterListStore";
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import CardCollection from "../../../framework/components/card/CardCollection";
import {RichContent} from "../../../framework/components/RichContent";
import {findAllTheaters} from "../theaterApi";
import * as actions from "./theaterListActions";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";

export interface TheaterListProps {
  path: string
}

export default class TheaterList extends StoreListenerComponent<TheaterListProps, TheaterListState> {
  constructor() {
    super(theaterListStore())
  }

  componentDidMount() {
    super.componentDidMount()
    findAllTheaters()
      .then(actions.updateList)
  }

  render(props: TheaterListProps, state: TheaterListState) {
    if (this.mounted) {
      return (
        <Page title="Liste des théâtres" role="admin">
          <Loading loading={!state.theaters} message={messages.production.list.loading}>
            {state.theaters && this.renderCards(state)}
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderCards(state: TheaterListState) {
    return (
      <CardCollection columns={2}>
        {state.theaters.map(theater => (
          <Card title={theater.name}>
            <CardContent>
              <p>📍 {theater.address}</p>
              <RichContent content={theater.content} limit={100} />
            </CardContent>
            <CardAction href={`/production/theaters/update/${theater.id}`}>Éditer</CardAction>
            <CardAction className="delete">Supprimer</CardAction>
          </Card>
        ))}
      </CardCollection>
    )
  }
}