import preact from "preact"
import {AsyncPage} from "../../../framework/components/Page"
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import CardCollection from "../../../framework/components/card/CardCollection";
import * as actions from "./adhesionListActions";
import {
  acceptAdhesion, downloadAdhesions, findAdhesionsByPeriod, findPeriodAdhesion,
  refuseAdhesion
} from "../adhesionApi";
import {Adhesion} from "../adhesionModel";
import {shortPeriodFormat} from "../../../common/types/Period";
import {AdhesionListState, adhesionListStore} from "./adhesionListStore";
import {shortDateFormat} from "../../../framework/utils/dates";
import popinQuestion from "../../../framework/components/popin/PopinQuestion";
import {PrimaryButton} from "../../../framework/components/buttons";

export interface AdhesionListProps {
  path: string
  period?: string
}

export default class AdhesionList extends AsyncPage<AdhesionListProps, AdhesionListState> {
  constructor() {
    super(adhesionListStore)
  }

  initialize(props: AdhesionListProps) {
    return Promise.all([
      findPeriodAdhesion(props.period),
      findAdhesionsByPeriod(props.period)
    ]).then(([period, adhesions]) => actions.initialize({period, adhesions}))
  }

  getTitle(props: AdhesionListProps, state: AdhesionListState) {
    return `${shortPeriodFormat(state.period.period)} – Adhésions`
  }

  renderPage(props: AdhesionListProps, state: AdhesionListState) {
    return (
      <div>
        <PrimaryButton message="Tout télécharger en CSV" action={() => this.downloadCSV(props)} />
        {this.renderAdhesions(state.notAcceptedAdhesions, "En attente de validation")}
        {this.renderAdhesions(state.acceptedAdhesions, "Validées")}
      </div>
    )
  }

  renderAdhesions(adhesions: Array<Adhesion>, title: string) {
    if (adhesions && adhesions.length) {
      return (
        <div>
          <h2>{title}</h2>
          <CardCollection columns={2}>
            {adhesions.map(adhesion => this.renderAdhesion(adhesion))}
          </CardCollection>
        </div>
      )
    } else {
      return null
    }
  }

  renderAdhesion(adhesion: Adhesion) {
    return (
      <Card title={`${adhesion.user.firstName} ${adhesion.user.lastName}`}>
        <CardContent>
          <ul>
            {adhesion.members.map(member => (
              <li>
                {member.firstName} {member.lastName} ({shortDateFormat(member.birthday)}) : {member.activity}
              </li>
            ))}
          </ul>
        </CardContent>
        <CardAction action={() => this.acceptAdhesion(adhesion)}>Accepter</CardAction>
        <CardAction className="delete" action={() => this.refuseAdhesion(adhesion)}>Refuser</CardAction>
        <CardAction href={`/personal/profile/read/${adhesion.user.id}`} target="blank">Profile</CardAction>
      </Card>
    )
  }

  acceptAdhesion(adhesion: Adhesion) {
    acceptAdhesion(adhesion)
      .then(() => this.initialize(this.props))
  }

  refuseAdhesion(adhesion: Adhesion) {
    popinQuestion(
      "Voulez-vous vraiment refuser la demande d'adhésion ?",
      "Veuillez indiquer la raison pour laquelle vous refusez la demande, elle sera envoyée avec le mail de refus."
    )
      .then(reason => refuseAdhesion(adhesion, reason))
      .then(() => this.initialize(this.props))
  }

  downloadCSV(props: AdhesionListProps) {
    downloadAdhesions(props.period)
  }
}