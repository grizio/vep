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

export interface Adhesion {
  id: string
  user: AdhesionUser
  period: PeriodAdhesion
  accepted: boolean
  members: Array<AdhesionMember>
}

export interface AdhesionUser {
  id: string
  email: string
  firstName: string
  lastName: string
}

export interface AdhesionMember {
  firstName: string
  lastName: string
  birthday: Date
  activity: string
}

export interface RequestAdhesion {
  period: string
  members: Array<AdhesionMember>
}