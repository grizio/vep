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
      commonVerifications.verifyIsBefore(playCreation.reservationEndDate, playCreation.date),
      verifyPrices(playCreation.prices)
    ) map {
      case theater ~ date ~ reservationEndDate ~ prices => Play(
        UUID.randomUUID().toString,
        theater.id, date, reservationEndDate, prices
      )
    }
  }

  private def verifyPrices(prices: Seq[PlayPrice]): Validation[Seq[PlayPrice]] = {
    Validation.all(
      commonVerifications.verifyNonEmptySeq(prices),
      Validation.sequence(prices.map(verifyPrice))
    ) map { _ => prices }
  }

  private def verifyPrice(price: PlayPrice): Validation[PlayPrice] = {
    Validation.all(
      commonVerifications.verifyNonBlank(price.name),
      commonVerifications.verifyIsPositive(price.value)
    ) map { _ => price }
  }
}
