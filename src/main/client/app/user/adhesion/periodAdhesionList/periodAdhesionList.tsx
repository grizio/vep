import preact from "preact"
import {AsyncPage} from "../../../framework/components/Page"
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import CardCollection from "../../../framework/components/card/CardCollection";
import * as actions from "./periodAdhesionListActions";
import {PeriodAdhesionListState, periodAdhesionListStore} from "./periodAdhesionListStore";
import {findAllPeriodAdhesion} from "../adhesionApi";
import {PeriodAdhesion} from "../adhesionModel";
import {shortPeriodFormat} from "../../../common/types/Period";

export interface PeriodAdhesionListProps {
  path: string
}

export default class PeriodAdhesionList extends AsyncPage<PeriodAdhesionListProps, PeriodAdhesionListState> {
  constructor() {
    super(periodAdhesionListStore)
  }

  initialize() {
    return findAllPeriodAdhesion().then(actions.updateList)
  }

  getTitle() {
    return "Liste des périodes d'adhésion"
  }

  renderPage(props: PeriodAdhesionListProps, state: PeriodAdhesionListState) {
    return (
      <div>
      {this.renderPeriodsAdhesion(state.currentPeriodsAdhesion, "En cours")}
      {this.renderPeriodsAdhesion(state.futurePeriodsAdhesion, "À venir")}
      {this.renderPeriodsAdhesion(state.passedPeriodsAdhesion, "Passées")}
    </div>
    )
  }

  renderPeriodsAdhesion(periodsAdhesion: Array<PeriodAdhesion>, title: string) {
    if (periodsAdhesion && periodsAdhesion.length) {
      return (
        <div>
          <h2>{title}</h2>
          <CardCollection columns={2}>
            {periodsAdhesion.map(this.renderPeriodAdhesion)}
          </CardCollection>
        </div>
      )
    } else {
      return null
    }
  }

  renderPeriodAdhesion(periodAdhesion: PeriodAdhesion) {
    return (
      <Card title={shortPeriodFormat(periodAdhesion.period)}>
        <CardContent>
          <p>Inscriptions : {shortPeriodFormat(periodAdhesion.registrationPeriod)}</p>
          <p>Activités :</p>
          <ul>
            {periodAdhesion.activities.map(activity => <li>{activity}</li>)}
          </ul>
        </CardContent>
        <CardAction href={`/adhesions/update/${periodAdhesion.id}`}>Éditer</CardAction>
        <CardAction href={`/adhesions/list/${periodAdhesion.id}`}>Liste des adhésions</CardAction>
      </Card>
    )
  }
}