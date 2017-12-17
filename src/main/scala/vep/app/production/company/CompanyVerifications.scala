package vep.app.production.company

import vep.app.common.verifications.CommonVerifications
import vep.framework.validation.Validation

class CompanyVerifications(
  commonVerifications: CommonVerifications
) {

  def verify(company: Company, companyId: String): Validation[Company] = {
    commonVerifications.verifyEquals(company.id, companyId)
      .map { _ => company }
  }
}
