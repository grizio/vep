package vep.test.controller.theater

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultError, ResultSuccessEntity}
import vep.service.AnormImplicits
import vep.test.controller.VepControllersDBInMemoryComponent


@RunWith(classOf[JUnitRunner])
class TheaterControllerIsLockedSpecification extends Specification with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "theater/theater-default",
      "theater/theater-session"
    )
  }

  sequential ^ "Specifications of TheaterController" >> {
    "isLocked should" >> {
      lazy val validTheaterLocked = "existing-theater"
      lazy val validTheaterUnlocked = "my-theater"
      lazy val invalidTheater = "unknown-theater"

      prepare()

      "return a success with true on valid theater locked" >> {
        val result = theaterController.isLocked(validTheaterLocked)
        result === Right(ResultSuccessEntity(true))
      }

      "return a success with false on valid theater not locked" >> {
        val result = theaterController.isLocked(validTheaterUnlocked)
        result === Right(ResultSuccessEntity(false))
      }

      "return an error on unknown theater" >> {
        val result = theaterController.isLocked(invalidTheater)
        result === Left(ResultError(ErrorCodes.undefinedTheater))
      }
    }
  }
}
