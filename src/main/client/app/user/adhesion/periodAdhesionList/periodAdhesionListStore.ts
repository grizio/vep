import {LocalStore} from "fluxx"
import * as actions from "./periodAdhesionListActions"
import {copy} from "../../../framework/utils/object";
import {PeriodAdhesion} from "../adhesionModel";
import {isCurrent, isFuture, isPassed} from "../../../common/types/Period";

export interface PeriodAdhesionListState {
  currentPeriodsAdhesion: Array<PeriodAdhesion>
  futurePeriodsAdhesion: Array<PeriodAdhesion>
  passedPeriodsAdhesion: Array<PeriodAdhesion>
}

const initialState: PeriodAdhesionListState = {
  currentPeriodsAdhesion: null,
  futurePeriodsAdhesion: null,
  passedPeriodsAdhesion: null
}

export const periodAdhesionListStore = () => LocalStore(initialState, on => {
  on(actions.updateList, (state, periodsAdhesion) => {
    return copy(state, {
      currentPeriodsAdhesion: periodsAdhesion.filter(_ => isCurrent(_.period)),
      futurePeriodsAdhesion: periodsAdhesion.filter(_ => isFuture(_.period)),
      passedPeriodsAdhesion: periodsAdhesion.filter(_ => isPassed(_.period))
    })
  })
})