import {request} from "../../framework/utils/http";
import {copy} from "../../framework/utils/object";
import {localDateTimeIsoFormat, localIsoFormatToDate} from "../../framework/utils/dates";
import {Play, PlayCreation, PlayMeta, PlayUpdate, PlayWithDependencies} from "./playModel";

export function findPlaysByShow(company: string, show: string): Promise<Array<Play>> {
  return request<Array<Play>>({
    method: "GET",
    url: `production/companies/${company}/shows/${show}/plays`
  }).then(plays => plays.map(jsonToPlay))
}

export function findPlay(company: string, show: string, id: string): Promise<Play> {
  return request<Play>({
    method: "GET",
    url: `production/companies/${company}/shows/${show}/plays/${id}`
  }).then(jsonToPlay)
}

export function findNextPlays(): Promise<Array<PlayMeta>> {
  return request<Array<PlayMeta>>({
    method: "GET",
    url: `production/plays/next`
  }).then(plays => plays.map(jsonToPlayMeta))
}

export function findNextPlaysWithDependencies(): Promise<Array<PlayWithDependencies>> {
  return request<Array<PlayWithDependencies>>({
    method: "GET",
    url: `production/plays/nextFull`
  }).then(plays => plays.map(jsonToPlayWithDependencies))
}

export function createPlay(company: string, show: string, play: PlayCreation) {
  return request({
    method: "POST",
    url: `production/companies/${company}/shows/${show}/plays`,
    entity: copy(play, {
      date: localDateTimeIsoFormat(play.date),
      reservationEndDate: localDateTimeIsoFormat(play.reservationEndDate)
    } as any)
  })
}

export function updatePlay(company: string, show: string, play: PlayUpdate) {
  return request({
    method: "PUT",
    url: `production/companies/${company}/shows/${show}/plays/${play.id}`,
    entity: copy(play, {
      date: localDateTimeIsoFormat(play.date),
      reservationEndDate: localDateTimeIsoFormat(play.reservationEndDate)
    } as any)
  })
}

export function deletePlay(company: string, show: string, play: Play) {
  return request({
    method: "DELETE",
    url: `production/companies/${company}/shows/${show}/plays/${play.id}`
  })
}

export function jsonToPlay(play: Play): Play {
  return copy(play, {
    date: localIsoFormatToDate(play.date as any),
    reservationEndDate: localIsoFormatToDate(play.reservationEndDate as any)
  })
}

export function jsonToPlayMeta(play: PlayMeta): PlayMeta {
  return copy(play, {
    date: localIsoFormatToDate(play.date as any)
  })
}

export function jsonToPlayWithDependencies(play: PlayWithDependencies): PlayWithDependencies {
  return copy(play, {
    play: jsonToPlay(play.play)
  })
}