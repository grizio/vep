import {LocalStore} from "fluxx"
import * as actions from "./profilePageActions"
import {copy} from "../../../framework/utils/object";
import {Profile} from "../profileModel";
import {Adhesion} from "../../adhesion/adhesionModel";

export interface ProfilePageState {
  profile: Profile
  acceptedAdhesions: Array<Adhesion>
  notAcceptedAdhesions: Array<Adhesion>
}

const initialState: ProfilePageState = {
  profile: null,
  acceptedAdhesions: [],
  notAcceptedAdhesions: []
}

export const profilePageStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {profile, adhesions}) => {
    return copy(state, {
      profile: profile,
      acceptedAdhesions: adhesions.filter(_ => _.accepted),
      notAcceptedAdhesions: adhesions.filter(_ => !_.accepted)
    })
  })
})