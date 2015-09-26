package vep.test.router.api.reservation

import org.junit.runner.RunWith
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.common.ErrorCodes
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ReservationRouterReservedPlacesAsNumberSpecification extends Specification with VepRouterDBInMemorySpecification with JsonMatchers {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  sequential ^ "Specification of ReservationRouter" >> {
    "reserved places as number" >> {
      val validUrlDynamic = "/reservation/existing-theater-dynamic/existing-session_2/number"
      val invalidUrlTheater = "/reservation/undefined-theater-dynamic/existing-session_1/number"
      val invalidUrlSession = "/reservation/existing-theater-dynamic/undefined-session_1/number"
      val validUrlFixed = "/reservation/existing-theater-fixed/existing-session_1/number"

      // Prepare here because no change
      prepare()

      "intercept a request to /reservation/<theater>/<session>/number as GET" >> {
        Get(validUrlDynamic) ~> route ~> check {
          handled === true
        }
      }

      "returns a code 404 when undefined theater" >> {
        Get(invalidUrlTheater) ~> route ~> check {
          (status === StatusCodes.NotFound) and (responseAs[String] === ErrorCodes.undefinedTheater.toString)
        }
      }

      "returns a code 404 when undefined session" >> {
        Get(invalidUrlSession) ~> route ~> check {
          (status === StatusCodes.NotFound) and (responseAs[String] === ErrorCodes.undefinedSession.toString)
        }
      }

      "returns a code 400 when fixed theater" >> {
        Get(validUrlFixed) ~> route ~> check {
          (status === StatusCodes.BadRequest) and (responseAs[String] === ErrorCodes.fixedTheater.toString)
        }
      }

      "returns a code 200 with number when success (dynamic theater)" >> {
        Get(validUrlDynamic) ~> route ~> check {
          (status === StatusCodes.OK) and (responseAs[String] === "6")
        }
      }
    }
  }
}
