package vep.test.router.api.reservation

import org.junit.runner.RunWith
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.session.{ReservationFormBody, ReservationFormResult, ReservationPriceForm}
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ReservationRouterFromSessionSpecification extends Specification with VepRouterDBInMemorySpecification with JsonMatchers {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  sequential ^ "Specification of ReservationRouter" >> {
    "from session" >> {
      val validUrlFixed = "/reservation/existing-theater-fixed/existing-session_1/list"
      lazy val validUrlDynamic = "/reservation/existing-theater-dynamic/existing-session_2/list"

      // Prepare here because no change
      prepare()

      "intercept a request to /reservation/<theater>/<session>/list as GET (fixed theater)" >> {
        Get(validUrlFixed) ~> addCredentials(validCredentialsAdmin) ~> route ~> check {
          handled === true
        }
      }

      "intercept a request to /reservation/<theater>/<session>/list as GET (dynamic theater)" >> {
        Get(validUrlDynamic) ~> addCredentials(validCredentialsAdmin) ~> route ~> check {
          handled === true
        }
      }

      "returns a code 401 when not connected" >> {
        Get(validUrlDynamic) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }

      "returns a code 403 when forbidden" >> {
        Get(validUrlDynamic) ~> addCredentials(validCredentialsUser) ~> route ~> check {
          status === StatusCodes.Forbidden
        }
      }

      "returns a code 200 with Array when success (dynamic theater)" >> {
        Get(validUrlDynamic) ~> addCredentials(validCredentialsAdmin) ~> route ~> check {
          (status === StatusCodes.OK) and (responseAs[String] must startWith("{"))
        }
      }

      "return a code 200 with id and pass when success (fixed theater)" >> {
        Get(validUrlFixed) ~> addCredentials(validCredentialsAdmin) ~> route ~> check {
          (status === StatusCodes.OK) and (responseAs[String] must startWith("{"))
        }
      }
    }
  }
}
