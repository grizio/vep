package vep.test.router.api.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.session.{SessionPriceForm, SessionFormBody}
import vep.test.router.api.VepRouterDBInMemorySpecification
import vep.test.utils.DateUtilsTest
import vep.utils.StringUtils

@RunWith(classOf[JUnitRunner])
class SessionRouterFindSpecification extends Specification with VepRouterDBInMemorySpecification {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/session-theater",
      "session/session-company",
      "session/session-show",
      "session/session-default"
    )
  }

  import InvalidSessionEntitiesImplicits._
  import spray.httpx.SprayJsonSupport._
  import vep.model.session.SessionImplicits._

  sequential ^ "Specification of SessionRouter" >> {
    "find" >> {
      lazy val validTheater = "existing-theater"
      lazy val validSession = "existing-session_1"
      lazy val validPassedSession = "existing-session_2"
      lazy val invalidSession = "unknown-session"
      lazy val validUrl = s"/session/$validTheater/$validSession"
      lazy val validPassedUrl = s"/session/$validTheater/$validPassedSession"
      lazy val invalidUrl = s"/session/$validTheater/$invalidSession"

      prepare()

      s"accepts a request to $validUrl as GET with valid theater and session and returns an entity" >> {
        Get(validUrl) ~> route ~> check {
          (handled === true) and (status === StatusCodes.OK)
        }
      }

      s"accepts a request to $validPassedUrl as GET with valid theater and passed session" >> {
        "returns Unauthorized if no authentication" >> {
          Get(validPassedUrl) ~> route ~> check {
            (handled === true) and (status === StatusCodes.Unauthorized)
          }
        }

        "returns Forbidden if authenticated as simple user" >> {
          Get(validPassedUrl) ~> addCredentials(validCredentialsUser) ~> route ~> check {
            (handled === true) and (status === StatusCodes.Forbidden)
          }
        }

        "returns the entity if authenticated as user with role 'sessions-manager'" >> {
          Get(validPassedUrl) ~> addCredentials(validCredentialsAdmin) ~> route ~> check {
            (handled === true) and (status === StatusCodes.OK) and (responseAs[String] must startWith("{"))
          }
        }
      }

      s"accepts a request to $invalidUrl as GET with valid theater and passed session and returns Not Found" >> {
        Get(invalidUrl) ~> route ~> check {
          (handled === true) and (status === StatusCodes.NotFound)
        }
      }
    }
  }
}