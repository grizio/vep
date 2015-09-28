package vep.test.controller.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultError, ResultSuccessEntity}
import vep.model.session.ReservedSeats
import vep.service.AnormImplicits
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}

@RunWith(classOf[JUnitRunner])
class ReservationControllerReservedPlacesAsNumberSpecification extends Specification with VepMatcher with ReservationMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of ReservationController" >> {
    "reservedPlacesAsNumber should" >> {
      val validTheaterFixed = "existing-theater-fixed"
      val validSessionFixed = "existing-session_1"
      val validTheaterDynamic = "existing-theater-dynamic"
      val validSessionDynamic = "existing-session_2"
      val invalidTheater = "undefined-theater"
      val invalidSession = "undefined-session"
      val validSessionNoReservation = "existing-session_3"

      val expected = 6

      // No database change, we prepare it at first.
      prepare()

      "returns the number when valid (dynamic theater)" >> {
        val result = reservationController.reservedPlacesAsNumber(validTheaterDynamic, validSessionDynamic)
        (result must beRight[ResultSuccessEntity[Int]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[Int]]].b.entity === expected)
      }

      "returns 0 when valid and no reservation (dynamic theater)" >> {
        val result = reservationController.reservedPlacesAsNumber(validTheaterDynamic, validSessionNoReservation)
        (result must beRight[ResultSuccessEntity[Int]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[Int]]].b.entity === 0)
      }

      "returns an error when theater is invalid" >> {
        val result = reservationController.reservedPlacesAsNumber(invalidTheater, validSessionDynamic)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.undefinedTheater)
      }

      "returns an error when theater is fixed" >> {
        val result = reservationController.reservedPlacesAsNumber(validTheaterFixed, validSessionDynamic)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.fixedTheater)
      }

      "returns an error when session is invalid" >> {
        val result = reservationController.reservedPlacesAsNumber(validTheaterDynamic, invalidSession)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.undefinedSession)
      }
    }
  }
}
