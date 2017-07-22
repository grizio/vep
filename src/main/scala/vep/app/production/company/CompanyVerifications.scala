package vep.app.production.company

import java.util.UUID

import vep.app.common.CommonVerifications
import vep.app.production.theater.{Seat, Theater, TheaterCreation, TheaterMessage}
import vep.framework.validation.{Invalid, Valid, Validation, ~}

class CompanyVerifications(
  commonVerifications: CommonVerifications
) {
  def verifyCreation(companyCreation: CompanyCreation): Validation[Company] = {
    Validation.all(
      commonVerifications.verifyNonBlank(companyCreation.name),
      commonVerifications.verifyNonBlank(companyCreation.address),
      commonVerifications.verifyNonBlank(companyCreation.content)
    ) map {
      case name ~ address ~ content => Company(
        UUID.randomUUID().toString,
        name, address, companyCreation.isVep, content
      )
    }
  }

  def verify(company: Company, companyId: String): Validation[Company] = {
    Validation.all(
      commonVerifications.verifyEquals(company.id, companyId),
      commonVerifications.verifyNonBlank(company.name),
      commonVerifications.verifyNonBlank(company.address),
      commonVerifications.verifyNonBlank(company.content)
    ) map { _ => company }
  }
}
