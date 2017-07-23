package vep.app.production.company.show

import java.util.UUID

import vep.app.common.verifications.CommonVerifications
import vep.framework.validation.{Validation, ~}

class ShowVerifications(
  commonVerifications: CommonVerifications
) {
  def verifyCreation(showCreation: ShowCreation): Validation[Show] = {
    Validation.all(
      commonVerifications.verifyNonBlank(showCreation.title),
      commonVerifications.verifyNonBlank(showCreation.author),
      commonVerifications.verifyNonBlank(showCreation.director),
      commonVerifications.verifyNonBlank(showCreation.content)
    ) map {
      case title ~ author ~ director ~ content => Show(
        UUID.randomUUID().toString,
        title, author, director, content
      )
    }
  }

  def verify(show: Show, showId: String): Validation[Show] = {
    Validation.all(
      commonVerifications.verifyEquals(show.id, showId),
      commonVerifications.verifyNonBlank(show.title),
      commonVerifications.verifyNonBlank(show.author),
      commonVerifications.verifyNonBlank(show.director),
      commonVerifications.verifyNonBlank(show.content)
    ) map { _ => show }
  }
}
