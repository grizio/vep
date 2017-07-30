import {request} from "../../framework/utils/http";
import {PeriodAdhesionCreation} from "./adhesionModel";
import {copy} from "../../framework/utils/object";
import {periodToJson} from "../../common/types/Period";

export function createPeriodAdhesion(periodAdhesion: PeriodAdhesionCreation) {
  return request({
    method: "POST",
    url: "user/adhesions",
    entity: periodAdhesionCreationToJson(periodAdhesion)
  })
}

function periodAdhesionCreationToJson(periodAdhesion: PeriodAdhesionCreation): PeriodAdhesionCreation {
  return copy(periodAdhesion, {
    period: periodToJson(periodAdhesion.period),
    registrationPeriod: periodToJson(periodAdhesion.registrationPeriod)
  })
}