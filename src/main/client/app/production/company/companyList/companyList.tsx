import preact from "preact"
import Page from "../../../framework/components/Page"
import StoreListenerComponent from "../../../framework/utils/dom"
import {CompanyListState, companyListStore} from "./companyListStore";
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import CardCollection from "../../../framework/components/card/CardCollection";
import {RichContent} from "../../../framework/components/RichContent";
import {deleteCompany, findAllCompanies} from "../companyApi";
import * as actions from "./companyListActions";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import {Company} from "../companyModel";

export interface CompanyListProps {
  path: string
}

export default class CompanyList extends StoreListenerComponent<CompanyListProps, CompanyListState> {
  constructor() {
    super(companyListStore())
  }

  componentDidMount() {
    super.componentDidMount()
    this.initialize()
  }

  render(props: CompanyListProps, state: CompanyListState) {
    if (this.mounted) {
      return (
        <Page title="Liste des troupes" role="admin">
          <Loading loading={!state.vepCompanies} message={messages.production.company.list.loading}>
            {state.vepCompanies && state.vepCompanies.length && this.renderCards(state.vepCompanies, "Troupes de Voir & Entendre")}
            {state.notVepCompanies && state.notVepCompanies.length && this.renderCards(state.notVepCompanies, "Troupes externes")}
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderCards(companies: Array<Company>, title: string) {
    return (
      <div>
        <h2>{title}</h2>
        <CardCollection columns={2}>
          {companies.map(company => (
            <Card title={company.name}>
              <CardContent>
                <p>📍 {company.address}</p>
                <RichContent content={company.content} limit={100}/>
              </CardContent>
              <CardAction href={`/production/companies/${company.id}`}>Plus d'information</CardAction>
              <CardAction href={`/production/companies/${company.id}/shows/create`}>Nouvelle pièce</CardAction>
              <CardAction href={`/production/companies/update/${company.id}`}>Éditer</CardAction>
              <CardAction className="delete" action={() => this.deleteCompany(company)}>Supprimer</CardAction>
            </Card>
          ))}
        </CardCollection>
      </div>
    )
  }

  initialize() {
    findAllCompanies().then(actions.updateList)
  }

  deleteCompany(company: Company) {
    deleteCompany(company).then(() => this.initialize())
  }
}