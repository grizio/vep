package vep.test.router.api.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class SessionRouterSearchSpecification extends Specification with VepRouterDBInMemorySpecification {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/session-theater",
      "session/session-company",
      "session/session-show",
      "session/session-search"
    )
  }

  sequential ^ "Specification of SessionRouter" >> {
    "search" >> {
      lazy val validSimpleUrl = "/sessions"
      lazy val validTheaterUrl = validSimpleUrl + "?t=existing-theater"
      lazy val validShowUrl = validSimpleUrl + "?s=is"
      lazy val validStartDateUrl = validSimpleUrl + "?sd=2100-05-01"
      lazy val validEndDateUrl = validSimpleUrl + "?ed=2100-02-01"
      lazy val validOrderUrl = validSimpleUrl + "?o=t"
      lazy val validPageUrl = validSimpleUrl + "?p=2"
      lazy val invalidParameter = validSimpleUrl + "?sd=20000101000000"

      prepare()

      s"intercept a request to $validSimpleUrl as GET with valid entity" >> {
        Get(validSimpleUrl) ~> route ~> check {
          handled === true and status === StatusCodes.OK
        }
      }

      s"intercept a request to $validTheaterUrl as GET with valid entity" >> {
        Get(validTheaterUrl) ~> route ~> check {
          handled === true and status === StatusCodes.OK
        }
      }

      s"intercept a request to $validShowUrl as GET with valid entity" >> {
        Get(validShowUrl) ~> route ~> check {
          handled === true and status === StatusCodes.OK
        }
      }

      s"intercept a request to $validStartDateUrl as GET with valid entity" >> {
        Get(validStartDateUrl) ~> route ~> check {
          handled === true and status === StatusCodes.OK
        }
      }

      s"intercept a request to $validEndDateUrl as GET with valid entity" >> {
        Get(validEndDateUrl) ~> route ~> check {
          handled === true and status === StatusCodes.OK
        }
      }

      s"intercept a request to $validOrderUrl as GET with valid entity" >> {
        Get(validOrderUrl) ~> route ~> check {
          handled === true and status === StatusCodes.OK
        }
      }

      s"intercept a request to $validPageUrl as GET with valid entity" >> {
        Get(validPageUrl) ~> route ~> check {
          handled === true and status === StatusCodes.OK
        }
      }

      s"intercept a request to $invalidParameter as GET but return error" >> {
        Get(invalidParameter) ~> route ~> check {
          handled === true and status === StatusCodes.BadRequest
        }
      }
    }
  }
}
