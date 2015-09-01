package vep.test.router.api.theater

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class TheaterRouterIsLockedSpecification extends Specification with VepRouterDBInMemorySpecification {
  def prepare() = prepareDB("theater/theater-default", "theater/theater-session")

  sequential ^ "Specification of TheaterRouter" >> {
    "isLocked" >> {
      val validLockedUrl = "/theater/existing-theater/locked"
      val validUnlockedUrl = "/theater/my-theater/locked"
      val invalidUrl = "/theater/unknown-theater/locked"

      prepare()

      s"intercept a request to $validLockedUrl as GET and return true" >> {
        Get(validLockedUrl) ~> route ~> check {
          (handled === true) and (status === StatusCodes.OK) and (responseAs[String] === "true")
        }
      }

      s"intercept a request to $validUnlockedUrl as GET and return true" >> {
        Get(validUnlockedUrl) ~> route ~> check {
          (handled === true) and (status === StatusCodes.OK) and (responseAs[String] === "false")
        }
      }

      s"intercept a request to $invalidUrl as GET and return true" >> {
        Get(invalidUrl) ~> route ~> check {
          (handled === true) and (status === StatusCodes.NotFound)
        }
      }
    }
  }
}
