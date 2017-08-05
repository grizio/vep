import {LocalStore} from "fluxx"
import * as actions from "./adhesionListActions"
import {copy} from "../../../framework/utils/object";
import {Adhesion, PeriodAdhesion} from "../adhesionModel";

export interface AdhesionListState {
  period: PeriodAdhesion
  acceptedAdhesions: Array<Adhesion>
  notAcceptedAdhesions: Array<Adhesion>
}

const initialState: AdhesionListState = {
  period: null,
  acceptedAdhesions: [],
  notAcceptedAdhesions: []
}

export const adhesionListStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {period, adhesions}) => {
    return copy(state, {
      period: period,
      acceptedAdhesions: adhesions.filter(_ => _.accepted),
      notAcceptedAdhesions: adhesions.filter(_ => !_.accepted)
    })
  })
})