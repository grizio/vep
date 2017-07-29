package vep.app.common.page

import vep.app.common.verifications.CommonVerifications
import vep.framework.validation.{Invalid, Valid, Validation, ~}

class PageVerifications(
  commonVerifications: CommonVerifications,
  pageService: PageService
) {
  def verify(page: Page, canonical: String): Validation[Page] = {
    (
      commonVerifications.verifyEquals(page.canonical, canonical)
        ~ commonVerifications.verifyCanonical(page.canonical)
        ~ commonVerifications.verifyNonBlank(page.title)
        ~ commonVerifications.verifyNonBlank(page.content)
        ~ verifyNotDuplicatedCanonical(page.canonical)
      ) map { _ => page }
  }

  private def verifyNotDuplicatedCanonical(canonical: String): Validation[String] = {
    pageService.find(canonical) match {
      case Some(_) => Invalid(PageMessages.duplicatedCanonical)
      case None => Valid(canonical)
    }
  }
}
