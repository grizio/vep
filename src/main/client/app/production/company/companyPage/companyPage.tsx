import preact from "preact"
import Page from "../../../framework/components/Page"
import StoreListenerComponent from "../../../framework/utils/dom"
import CardCollection from "../../../framework/components/card/CardCollection";
import {RichContent} from "../../../framework/components/RichContent";
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import {CompanyPageState, companyPageStore} from "./companyPageStore";
import {findCompany, findShowsByCompany} from "../companyApi";
import * as actions from "./companyPageActions";
import {Company, Show} from "../companyModel";

export interface CompanyPageProps {
  path: string
  id?: string
}

export default class CompanyPage extends StoreListenerComponent<CompanyPageProps, CompanyPageState> {
  constructor() {
    super(companyPageStore())
  }

  componentDidMount() {
    super.componentDidMount()
    this.initialize()
  }

  initialize() {
    Promise.all([
      findCompany(this.props.id),
      findShowsByCompany(this.props.id)
    ])
      .then(([company, shows]) => actions.initialize({company, shows}))
  }

  render(props: CompanyPageProps, state: CompanyPageState) {
    if (this.mounted) {
      return (
        <Page title={state.company ? state.company.name : ""} role="admin">
          <Loading loading={state.loading} message={messages.production.company.page.loading}>
            {!state.loading && this.renderCompany(state.company)}
            {!state.loading && this.renderShows(state.shows, state.company)}
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderCompany(company: Company) {
    return (
      <section>
        <h2>Présentation de {company.name}</h2>
        <div class="row">
          <div class="col-4">
            <RichContent content={company.content}/>
          </div>
          <div class="col-1">
            <Card title={company.name}>
              <CardContent>
                <p>{company.isVep ? "Troupe de Voir & Entendre" : "Troupe externe"}</p>
                <p>📍 {company.address}</p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>
    )
  }

  renderShows(shows: Array<Show>, company: Company) {
    return (
      <section>
        <h2>Pièces jouées par {company.name}</h2>
        <CardCollection columns={3}>
          {shows.map(show => (
            <Card title={show.title}>
              <CardContent>
                <p>Écrit par <strong>{show.author}</strong> et mis en scène par <strong>{show.director}</strong></p>
                <RichContent content={show.content} limit={100}/>
              </CardContent>
              <CardAction
                href={`/production/companies/${company.id}/shows/update/${show.id}`}>Éditer</CardAction>
              <CardAction className="delete" action={() => this.deleteShow(show)}>Supprimer</CardAction>
            </Card>
          ))}
        </CardCollection>
      </section>
    )
  }

  deleteShow(show: Show) {
    //deleteShow(show).then(() => this.initialize())
  }
}