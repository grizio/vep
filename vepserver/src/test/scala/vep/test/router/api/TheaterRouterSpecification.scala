package vep.test.router.api

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import spray.json.DefaultJsonProtocol
import vep.model.theater.TheaterFormBody


case class InvalidTheaterFormBody(name: String)

object InvalidTheaterEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidTheaterFormBody = jsonFormat1(InvalidTheaterFormBody)
}

@RunWith(classOf[JUnitRunner])
class TheaterRouterSpecification extends Specification with VepRouterSpecification {

  import spray.httpx.SprayJsonSupport._
  import vep.model.theater.TheaterImplicits._
  import InvalidTheaterEntitiesImplicits._

  "Specification of TheaterRouter" >> {
    "create" >> {
      val validUrl = "/theater/my-new-theater"
      val validEntity = TheaterFormBody(
        name = "My new theater",
        address = "Somewhere in the middle",
        content = Some("A sample content"),
        fixed = false,
        plan = None,
        maxSeats = Some(10)
      )
      val validEntityWithErrors = validEntity.copy(name = "")
      val invalidEntity = InvalidTheaterFormBody("")

      "intercept a request to /theater/<canonical> as POST with valid entity" >> {
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /theater/<canonical> as POST when invalid entity" >> {
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

    "update" >> {
      val validUrl = "/theater/my-theater"
      val validEntity = TheaterFormBody(
        name = "My existing theater",
        address = "Somewhere in the middle",
        content = Some("A sample content"),
        fixed = false,
        plan = None,
        maxSeats = Some(10)
      )
      val validEntityWithErrors = validEntity.copy(name = "")
      val invalidEntity = InvalidTheaterFormBody("")

      "intercept a request to /theater/<canonical> as PUT with valid entity" >> {
        Put(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /theater/<canonical> as PUT when invalid entity" >> {
        Put(validUrl, invalidEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        Put(validUrl, validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "returns a code 403 when authenticated but not authorized" >> {
        Put(validUrl, validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }
      "returns a code 400 with map when error(s)" >> {
        Put(validUrl, validEntityWithErrors) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }
      "return a code 200 with nothing when success" >> {
        Put(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] === "null")
        }
      }
    }

    "list" >> {
      val validUrl: String = "/theaters"
      "intercept a request to /theaters as GET" >> {
        Get(validUrl) ~> route ~> check {
          handled === true
        }
      }
      "refuse a request to /theaters as POST" >> {
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
