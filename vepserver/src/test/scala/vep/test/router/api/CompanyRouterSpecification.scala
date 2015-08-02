package vep.test.router.api

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import spray.json.DefaultJsonProtocol
import vep.model.company.CompanyFormBody
import vep.model.theater.TheaterFormBody


case class InvalidCompanyFormBody(name: String)

object InvalidCompanyEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidCompanyFormBody = jsonFormat1(InvalidCompanyFormBody)
}

@RunWith(classOf[JUnitRunner])
class CompanyRouterSpecification extends Specification with VepRouterDBInMemorySpecification {
  def prepare() = prepareDB("user/users-with-roles", "company/company-default")

  import spray.httpx.SprayJsonSupport._
  import vep.model.company.CompanyImplicits._
  import InvalidCompanyEntitiesImplicits._

  sequential ^ "Specification of CompanyRouter" >> {
    "create" >> {
      val validUrl = "/company/my-new-company"
      val validEntity = CompanyFormBody(
        name = "My new theater",
        address = Some("Somewhere in the middle"),
        isVep = false,
        content = Some("A sample content")
      )
      val validEntityWithErrors = validEntity.copy(name = "")
      val invalidEntity = InvalidCompanyFormBody("")

      "intercept a request to /company/<canonical> as POST with valid entity" >> {
        prepare
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /company/<canonical> as POST when invalid entity" >> {
        prepare
        Post(validUrl, invalidEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        prepare
        Post(validUrl, validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "returns a code 403 when authenticated but not authorized" >> {
        prepare
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }
      "returns a code 400 with map when error(s)" >> {
        prepare
        Post(validUrl, validEntityWithErrors) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }
      "return a code 200 with nothing when success" >> {
        prepare
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] === "null")
        }
      }
    }

    "update" >> {
      val validUrl = "/company/existing-company"
      val validEntity = CompanyFormBody(
        name = "Updated company",
        address = Some("Somewhere in the middle"),
        isVep = true,
        content = Some("A sample content")
      )
      val validEntityWithErrors = validEntity.copy(name = "")
      val invalidEntity = InvalidCompanyFormBody("")

      "intercept a request to /company/<canonical> as PUT with valid entity" >> {
        prepare
        Put(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /company/<canonical> as PUT when invalid entity" >> {
        prepare
        Put(validUrl, invalidEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        prepare
        Put(validUrl, validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "returns a code 403 when authenticated but not authorized" >> {
        prepare
        Put(validUrl, validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }
      "returns a code 400 with map when error(s)" >> {
        prepare
        Put(validUrl, validEntityWithErrors) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }
      "return a code 200 with nothing when success" >> {
        prepare
        Put(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] === "null")
        }
      }
    }

    "list" >> {
      val validUrl: String = "/companies"
      "intercept a request to /companies as GET" >> {
        prepare
        Get(validUrl) ~> route ~> check {
          handled === true
        }
      }
      "refuse a request to /companies as POST" >> {
        prepare
        Post(validUrl) ~> route ~> check {
          handled === false
        }
      }
      "return a code 200 with a list when success" >> {
        prepare
        Get(validUrl) ~> route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] must startWith("["))
        }
      }
    }
  }
}
