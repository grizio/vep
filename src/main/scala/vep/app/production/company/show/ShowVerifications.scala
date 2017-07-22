package vep.app.production.company.show

import java.util.UUID

import vep.app.common.CommonVerifications
import vep.framework.validation.{Validation, ~}

class ShowVerifications(
  commonVerifications: CommonVerifications
) {
  def verifyCreation(companyCreation: ShowCreation): Validation[Show] = {
    Validation.all(
      commonVerifications.verifyNonBlank(companyCreation.title),
      commonVerifications.verifyNonBlank(companyCreation.author),
      commonVerifications.verifyNonBlank(companyCreation.director),
      commonVerifications.verifyNonBlank(companyCreation.content)
    ) map {
      case title ~ author ~ director ~ content => Show(
        UUID.randomUUID().toString,
        title, author, director, content
      )
    }
  }
}
