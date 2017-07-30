import {request} from "../../framework/utils/http";
import {PeriodAdhesion, PeriodAdhesionCreation} from "./adhesionModel";
import {copy} from "../../framework/utils/object";
import {jsonToPeriod, periodToJson} from "../../common/types/Period";

export function findAllPeriodAdhesion(): Promise<Array<PeriodAdhesion>> {
  return request<Array<PeriodAdhesion>>({
    method: "GET",
    url: "user/adhesions"
  }).then(_ => _.map(jsonToPeriodAdhesion))
}

export function findPeriodAdhesion(id: string): Promise<PeriodAdhesion> {
  return request<PeriodAdhesion>({
    method: "GET",
    url: `user/adhesions/${id}`
  }).then(jsonToPeriodAdhesion)
}

export function createPeriodAdhesion(periodAdhesion: PeriodAdhesionCreation) {
  return request({
    method: "POST",
    url: "user/adhesions",
    entity: periodAdhesionCreationToJson(periodAdhesion)
  })
}

export function updatePeriodAdhesion(periodAdhesion: PeriodAdhesion) {
  return request({
    method: "PUT",
    url: `user/adhesions/${periodAdhesion.id}`,
    entity: periodAdhesionToJson(periodAdhesion)
  })
}

function periodAdhesionCreationToJson(periodAdhesion: PeriodAdhesionCreation): PeriodAdhesionCreation {
  return copy(periodAdhesion, {
    period: periodToJson(periodAdhesion.period),
    registrationPeriod: periodToJson(periodAdhesion.registrationPeriod)
  })
}

function periodAdhesionToJson(periodAdhesion: PeriodAdhesion): PeriodAdhesion {
  return copy(periodAdhesion, {
    period: periodToJson(periodAdhesion.period),
    registrationPeriod: periodToJson(periodAdhesion.registrationPeriod)
  })
}

function jsonToPeriodAdhesion(periodAdhesion: PeriodAdhesion): PeriodAdhesion {
  return copy(periodAdhesion, {
    period: jsonToPeriod(periodAdhesion.period),
    registrationPeriod: jsonToPeriod(periodAdhesion.registrationPeriod)
  })
}