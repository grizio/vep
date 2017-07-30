import {Period} from "../../common/types/Period";

export interface PeriodAdhesion {
  id: string
  period: Period
  registrationPeriod: Period
  activities: Array<string>
}

export interface PeriodAdhesionCreation {
  period: Period
  registrationPeriod: Period
  activities: Array<string>
}