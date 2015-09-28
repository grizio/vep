package vep.test.controller.show

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultError, ResultSuccessEntity}
import vep.model.show._
import vep.service.AnormImplicits
import vep.test.controller.VepControllersDBInMemoryComponent

@RunWith(classOf[JUnitRunner])
class ShowControllerDetailSpecification extends Specification with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB("show/show-default")
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of ShowController" >> {
    "detail should" >> {
      val validCanonical = "existing-show"
      val invalidCanonical = "unknown-show"
      val expectedShowDetail = ShowDetail(
        canonical = "existing-show",
        title = "Existing show",
        author = "This author",
        director = Some("This director"),
        company = "existing-company",
        duration = Some(120),
        content = Some("This is a content")
      )

      "Returns the detail a show with given canonical" >> {
        prepare()
        val result = showController.detail(validCanonical)
        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowDetail]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowDetail]]].b.entity must beEqualTo(expectedShowDetail))
      }

      "Returns an error when the show does not exist" >> {
        prepare()
        val result = showController.detail(invalidCanonical)
        (result must beAnInstanceOf[Left[ResultError, _]]) and
          (result.asInstanceOf[Left[ResultError, _]].a.code must beEqualTo(ErrorCodes.undefinedShow))
      }
    }
  }
}

