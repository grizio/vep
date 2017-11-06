import preact from "preact"
import {AsyncPage} from "../../../framework/components/Page"
import CardCollection from "../../../framework/components/card/CardCollection";
import {RichContent} from "../../../framework/components/RichContent";
import {AdminCardAction, Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import {ShowPageState, showPageStore} from "./showPageStore";
import * as actions from "./showPageActions";
import {SecondaryButton} from "../../../framework/components/buttons";
import {isAfterNow, longDateTimeFormat} from "../../../framework/utils/dates";
import {findCompany} from "../../company/companyApi";
import {findShow} from "../showApi";
import {deletePlay, findPlaysByShow} from "../../play/playApi";
import {Show} from "../showModel";
import {Company} from "../../company/companyModel";
import {Play} from "../../play/playModel";
import {OnGranted} from "../../../framework/components/Security";
import messages from "../../../framework/messages";
import popinConfirm from "../../../framework/components/popin/PopinConfirm";

export interface ShowPageProps {
  path: string
  company?: string
  id?: string
}

export default class ShowPage extends AsyncPage<ShowPageProps, ShowPageState> {
  constructor() {
    super(showPageStore)
  }

  initialize(props: ShowPageProps) {
    return Promise.all([
      findCompany(props.company),
      findShow(props.company, props.id),
      findPlaysByShow(props.company, props.id)
    ])
      .then(([company, show, plays]) => actions.initialize({company, show, plays}))
  }

  getTitle(props: ShowPageProps, state: ShowPageState) {
    return state.show ? state.show.title : ""
  }

  renderPage(props: ShowPageProps, state: ShowPageState) {
    return (
      <div>
        {this.renderShow(state)}
        {this.renderPlays(state)}
      </div>
    )
  }

  renderShow(state: ShowPageState) {
    return (
      <section>
        <h2>Présentation de {state.show.title}</h2>
        <div class="row">
          <div class="col-fill">
            {this.renderShowContent(state.show)}
            <UpdateShowButton companyId={state.company.id} showId={state.show.id} />
          </div>
          <div class="col-fix-350">
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
              <AdminCardAction
                href={`/production/companies/${state.company.id}/shows/${state.show.id}/plays/update/${play.id}`}>
                Éditer
              </AdminCardAction>
              <AdminCardAction className="delete" action={() => this.deletePlay(play)}>Supprimer</AdminCardAction>
            </Card>
          ))}
        </CardCollection>
        <CreatePlayButton companyId={state.company.id} showId={state.show.id} />
      </section>
    )
  }

  getFuturePlays(state: ShowPageState) {
    return state.plays
      .filter(play => isAfterNow(play.date))
  }

  deletePlay(play: Play) {
    const deletion = messages.production.show.page.plays.deletion;
    popinConfirm(deletion.title, deletion.message)
      .then( _ => deletePlay(this.state.company.id, this.state.show.id, play))
      .then(_ => this.initialize(this.props))
  }
}

interface CreatePlayButtonProps {
  companyId: string
  showId: string
}

const CreatePlayButton = OnGranted<CreatePlayButtonProps>((props: CreatePlayButtonProps) => {
  return (
    <SecondaryButton message="Ajouter une séance" href={`/production/companies/${props.companyId}/shows/${props.showId}/plays/create`}/>
  )
}, "admin")

interface UpdateShowButtonProps {
  companyId: string
  showId: string
}

const UpdateShowButton = OnGranted<UpdateShowButtonProps>((props: UpdateShowButtonProps) => {
  return (
    <SecondaryButton message="Modifier" href={`/production/companies/${props.companyId}/shows/update/${props.showId}`}/>
  )
}, "admin")