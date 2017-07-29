import preact from "preact"
import Page from "../../../framework/components/Page"
import StoreListenerComponent from "../../../framework/utils/dom"
import CardCollection from "../../../framework/components/card/CardCollection";
import {RichContent} from "../../../framework/components/RichContent";
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import {ShowPageState, showPageStore} from "./showPageStore";
import * as actions from "./showPageActions";
import {PrimaryButton} from "../../../framework/components/buttons";
import {isAfterNow, longDateTimeFormat} from "../../../framework/utils/dates";
import {findCompany} from "../../company/companyApi";
import {findShow} from "../showApi";
import {deletePlay, findPlaysByShow} from "../../play/playApi";
import {Show} from "../showModel";
import {Company} from "../../company/companyModel";
import {Play} from "../../play/playModel";

export interface ShowPageProps {
  path: string
  company?: string
  id?: string
}

export default class ShowPage extends StoreListenerComponent<ShowPageProps, ShowPageState> {
  constructor() {
    super(showPageStore())
  }

  componentDidMount() {
    super.componentDidMount()
    this.initialize()
  }

  initialize() {
    Promise.all([
      findCompany(this.props.company),
      findShow(this.props.company, this.props.id),
      findPlaysByShow(this.props.company, this.props.id)
    ])
      .then(([company, show, plays]) => actions.initialize({company, show, plays}))
  }

  render(props: ShowPageProps, state: ShowPageState) {
    if (this.mounted) {
      return (
        <Page title={state.show ? state.show.title : ""} role="admin">
          <Loading loading={state.loading} message={messages.production.show.page.loading}>
            {!state.loading && this.renderShow(state)}
            {!state.loading && this.renderPlays(state)}
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderShow(state: ShowPageState) {
    return (
      <section>
        <h2>Présentation de {state.show.title}</h2>
        <div class="row">
          <div class="col-4">
            {this.renderShowContent(state.show)}
          </div>
          <div class="col-1">
            {this.renderShowCard(state.show)}
            {this.renderCompanyCard(state.company)}
          </div>
        </div>
      </section>
    )
  }

  renderShowContent(show: Show) {
    return (
      <RichContent content={show.content}/>
    )
  }

  renderShowCard(show: Show) {
    return (
      <Card title={show.title}>
        <CardContent>
          <p>✍️ {show.author}</p>
          <p>🎭 {show.director}</p>
        </CardContent>
      </Card>
    )
  }

  renderCompanyCard(company: Company) {
    return (
      <Card title={company.name}>
        <CardContent>
          <p>{company.isVep ? "Troupe de Voir & Entendre" : "Troupe externe"}</p>
          <p>📍 {company.address}</p>
          <RichContent content={company.content} limit={100}/>
        </CardContent>
      </Card>
    )
  }

  renderPlays(state: ShowPageState) {
    return (
      <section>
        <h2>Prochaines séances pour {state.show.title}</h2>
        <CardCollection columns={3}>
          {this.getFuturePlays(state).map(play => (
            <Card title={longDateTimeFormat(play.date)}>
              <CardContent>
                <p>🏠 {play.theater.name}</p>
                <p>Tarifs :</p>
                <ul>
                  {play.prices.map(price =>
                    <li>{price.name}: {price.value}€ {price.condition ? `(${price.condition})` : ""}</li>
                  )}
                </ul>
              </CardContent>
              <CardAction href={`/production/companies/${state.company.id}/shows/${state.show.id}/plays/page/${play.id}`}>
                Réserver
              </CardAction>
              <CardAction href={`/production/companies/${state.company.id}/shows/${state.show.id}/plays/update/${play.id}`}>
                Éditer
              </CardAction>
              <CardAction className="delete" action={() => this.deletePlay(play)}>Supprimer</CardAction>
            </Card>
          ))}
        </CardCollection>
        <PrimaryButton message="Ajouter des séances" href={`/production/companies/${state.company.id}/shows/${state.show.id}/plays/create`}/>
      </section>
    )
  }

  getFuturePlays(state: ShowPageState) {
    return state.plays
      .filter(play => isAfterNow(play.date))
  }

  deletePlay(play: Play) {
    deletePlay(this.state.company.id, this.state.show.id, play).then(() => this.initialize())
  }
}