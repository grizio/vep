package vep.test.router.api.reservation

import org.junit.runner.RunWith
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.common.ErrorCodes
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ReservationRouterReservedPlacesAsPlanSpecification extends Specification with VepRouterDBInMemorySpecification with JsonMatchers {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  sequential ^ "Specification of ReservationRouter" >> {
    "reserved places as plan" >> {
      val validUrlFixed = "/reservation/existing-theater-fixed/existing-session_1/plan"
      val invalidUrlTheater = "/reservation/undefined-theater-fixed/existing-session_1/plan"
      val invalidUrlSession = "/reservation/existing-theater-fixed/undefined-session_1/plan"
      val validUrlDynamic = "/reservation/existing-theater-dynamic/existing-session_2/plan"

      // Prepare here because no change
      prepare()

      "intercept a request to /reservation/<theater>/<session>/plan as GET" >> {
        Get(validUrlFixed) ~> route ~> check {
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

      "returns a code 400 when dynamic theater" >> {
        Get(validUrlDynamic) ~> route ~> check {
          (status === StatusCodes.BadRequest) and (responseAs[String] === ErrorCodes.notFixedTheater.toString)
        }
      }

      "returns a code 200 with Array when success (dynamic theater)" >> {
        Get(validUrlFixed) ~> route ~> check {
          (status === StatusCodes.OK) and (responseAs[String] must startWith("{"))
        }
      }
    }
  }
}
