package vep.test.router.api.show

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.show.ShowFormBody
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ShowRouterCreateSpecification extends Specification with VepRouterDBInMemorySpecification {
  def prepare() = prepareDB("user/users-with-roles", "company/company-default")

  import InvalidShowEntitiesImplicits._
  import spray.httpx.SprayJsonSupport._
  import vep.model.show.ShowImplicits._

  sequential ^ "Specification of ShowRouter" >> {
    "create" >> {
      val validUrl = "/show/my-new-show"
      val validEntity = ShowFormBody(
        title = "My new title",
        author = "My new author",
        director = "My new director",
        company = "existing-company",
        duration = Some(120),
        content = Some("My content")
      )
      val validEntityWithErrors = validEntity.copy(title = "")
      val invalidEntity = InvalidShowFormBody("")

      "intercept a request to /show/<canonical> as POST with valid entity" >> {
        prepare()
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /show/<canonical> as POST when invalid entity" >> {
        prepare()
        Post(validUrl, invalidEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        prepare()
        Post(validUrl, validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "returns a code 403 when authenticated but not authorized" >> {
        prepare()
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }
      "returns a code 400 with map when error(s)" >> {
        prepare()
        Post(validUrl, validEntityWithErrors) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }
      "return a code 200 with nothing when success" >> {
        prepare()
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] === "null")
        }
      }
    }
  }
}