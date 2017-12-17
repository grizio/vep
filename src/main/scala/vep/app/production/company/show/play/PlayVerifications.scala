package vep.app.production.company.show.play

import java.util.UUID

import vep.app.common.verifications.CommonVerifications
import vep.app.production.theater.{TheaterMessage, TheaterService}
import vep.framework.validation._

class PlayVerifications(
  commonVerifications: CommonVerifications,
  theaterService: TheaterService
) {
  def verifyCreation(playCreation: PlayCreation): Validation[Play] = {
    Validation.all(
      commonVerifications.verifyIsDefined(theaterService.find(playCreation.theater), TheaterMessage.unknownTheater),
      commonVerifications.verifyIsAfterNow(playCreation.date),
      commonVerifications.verifyIsBefore(playCreation.reservationEndDate, playCreation.date)
    ) map {
      case theater ~ date ~ reservationEndDate => Play(
        UUID.randomUUID().toString,
        theater.id, date, reservationEndDate, playCreation.prices
      )
    }
  }

  def verify(play: Play, playId: String): Validation[Play] = {
    Validation.all(
      commonVerifications.verifyEquals(play.id, playId),
      commonVerifications.verifyIsDefined(theaterService.find(play.theater), TheaterMessage.unknownTheater),
      commonVerifications.verifyIsAfterNow(play.date),
      commonVerifications.verifyIsBefore(play.reservationEndDate, play.date)
    ) map { _ => play }
  }
}
