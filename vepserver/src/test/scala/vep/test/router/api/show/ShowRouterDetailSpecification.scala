package vep.test.router.api.show

import org.junit.runner.RunWith
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ShowRouterDetailSpecification extends Specification with VepRouterDBInMemorySpecification with JsonMatchers {
  def prepare() = prepareDB("show/show-default")

  sequential ^ "Specification of ShowRouter" >> {
    "detail should" >> {
      val validUrl = "/show/existing-show"
      val invalidUrl = "/show/undefined-show"

      "intercept a request to /show/<canonical> as GET with existing canonical and returns 200 with the entity" >> {
        prepare()
        Get(validUrl) ~>
          route ~> check {
          (status === StatusCodes.OK) and (responseAs[String] must
            /("canonical").andHave("existing-show") and
            /("title").andHave("Existing show") and
            /("author").andHave("This author") and
            /("director").andHave("This director") and
            /("company").andHave("existing-company") and
            /("duration").andHave(120) and
            /("content").andHave("This is a content")
            )
        }
      }

      "intercept a request to /show/<canonical> as GET with undefined canonical and returns 400" >> {
        prepare()
        Get(invalidUrl) ~>
          route ~> check {
          status === StatusCodes.NotFound
        }
      }
    }
  }
}