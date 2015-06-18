package vep.test.router.api

import org.specs2.mutable.Specification
import spray.http.StatusCodes
import spray.json.DefaultJsonProtocol
import vep.model.cms.PageFormBody


case class InvalidPageFormBody(title: String)

object InvalidCmsEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidPageFormBody = jsonFormat1(InvalidPageFormBody)
}

class CmsRouterSpecification extends Specification with VepRouterSpecification {

  import InvalidCmsEntitiesImplicits._
  import spray.httpx.SprayJsonSupport._
  import vep.model.cms.PageImplicits._

  "Specification of PageRouter" >> {
    "create" >> {
      val validUrl = "/cms/page/my-test-page"
      val validEntity = PageFormBody(None, "title", "content")
      val validEntityWithErrors = PageFormBody(None, "", "")
      val invalidEntity = InvalidPageFormBody("")

      "intercept a request to /cms/page/<canonical> as POST with valid entity" >> {
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /cms/page/<canonical> as POST when invalid entity" >> {
        Post(validUrl, invalidEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        Post(validUrl, validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "returns a code 403 when authenticated but not authorized" >> {
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }
      "returns a code 400 with map when error(s)" >> {
        Post(validUrl, validEntityWithErrors) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }
      "return a code 200 with nothing when success" >> {
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] === "null")
        }
      }
    }

    "list" >> {
      val validUrl: String = "/cms/pages"
      "intercept a request to /cms/pages as GET" >> {
        Get(validUrl) ~> route ~> check {
          handled === true
        }
      }
      "refuse a request to /cms/pages as POST" >> {
        Post(validUrl) ~> route ~> check {
          handled === false
        }
      }
      "return a code 200 with a list when success" >> {
        Get(validUrl) ~> route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] must startWith("["))
        }
      }
    }
  }
}
