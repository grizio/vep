import preact from "preact"
import {AsyncPage} from "../../../framework/components/Page"
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import * as actions from "./profilePageActions";
import {ProfilePageState, profilePageStore} from "./profilePageStore";
import {getCurrentProfile, getSpecificProfile} from "../profileApi";
import {getAdhesionsFromCurrentProfile, getAdhesionsFromSpecificProfile} from "../../adhesion/adhesionApi";
import {Profile} from "../profileModel";
import {Adhesion} from "../../adhesion/adhesionModel";
import CardCollection from "../../../framework/components/card/CardCollection";
import {shortPeriodFormat} from "../../../common/types/Period";
import {shortDateFormat} from "../../../framework/utils/dates";

export interface ProfilePageProps {
  path: string
  id?: string
}

export default class ProfilePage extends AsyncPage<ProfilePageProps, ProfilePageState> {
  constructor() {
    super(profilePageStore)
  }

  initialize(props: ProfilePageProps) {
    if (props.id) {
      return Promise.all([
        getSpecificProfile(props.id),
        getAdhesionsFromSpecificProfile(props.id)
      ])
        .then(([profile, adhesions]) => actions.initialize({profile, adhesions}))
    } else {
      return Promise.all([
        getCurrentProfile(),
        getAdhesionsFromCurrentProfile()
      ])
        .then(([profile, adhesions]) => actions.initialize({profile, adhesions}))
    }
  }

  getTitle(props: ProfilePageProps, state: ProfilePageState): string {
    if (props.id) {
      return "Profil de l'utilisateur"
    } else {
      return "Votre profil"
    }
  }

  renderPage(props: ProfilePageProps, state: ProfilePageState): preact.VNode {
    return <div>
      {this.renderGeneralInformation(props, state.profile)}
      {this.renderAdhesions(props, state)}
    </div>;
  }

  renderGeneralInformation(props: ProfilePageProps, profile: Profile) {
    return (
      <div>
        <h2>{props.id ? "Informations de l'utilisateur" : "Vos informations"}</h2>
        <Card title={`${profile.email}`}>
          <CardContent>
            <p>
              Nom: {profile.firstName} {profile.lastName}
            </p>
            <p>
              Adresse: {profile.address} {profile.zipCode} {profile.city}
            </p>
            <p>Numéros de téléphones :</p>
            <ul>
              {profile.phones.map(phone => (
                <li>{phone.name} : {phone.number}</li>
              ))}
            </ul>
          </CardContent>
          {props.id || <CardAction href="/personal/profile/update">Modifier</CardAction>}
        </Card>
      </div>
    )
  }

  renderAdhesions(props: ProfilePageProps, state: ProfilePageState) {
    return (
      <div>
        {this.renderAdhesionsCollection(
          state.notAcceptedAdhesions,
          props.id ? "Ses demandes d'adhésion en cours" : "Vos demandes d'adhésion en cours"
        )}
        {this.renderAdhesionsCollection(
          state.acceptedAdhesions,
          props.id ? "Ses adhésions" : "Vos adhésions"
        )}
      </div>
    )
  }

  renderAdhesionsCollection(adhesions: Array<Adhesion>, title: string) {
    if (adhesions && adhesions.length) {
      return (
        <div>
          <h2>{title}</h2>
          <CardCollection columns={2}>
            {adhesions.map(this.renderAdhesion)}
          </CardCollection>
        </div>
      )
    } else {
      return null
    }
  }

  renderAdhesion(adhesion: Adhesion) {
    return (
      <Card title={shortPeriodFormat(adhesion.period.period)}>
        <CardContent>
          <ul>
            {adhesion.members.map(member => (
              <li>
                {member.firstName} {member.lastName} ({shortDateFormat(member.birthday)}) : {member.activity}
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    )
  }
}