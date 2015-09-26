package vep.test.controller.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultError, ResultSuccessEntity}
import vep.model.session.ReservedSeats
import vep.service.AnormImplicits
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}

@RunWith(classOf[JUnitRunner])
class ReservationControllerReservedPlacesAsPlanSpecification extends Specification with VepMatcher with ReservationMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of ReservationController" >> {
    "reservedPlacesAsPlan should" >> {
      val validTheaterFixed = "existing-theater-fixed"
      val validSessionFixed = "existing-session_1"
      val validTheaterDynamic = "existing-theater-dynamic"
      val validSessionDynamic = "existing-session_2"
      val invalidTheater = "undefined-theater"
      val invalidSession = "undefined-session"

      lazy val expected = ReservedSeats(Seq("A1", "A2", "A3", "A4", "A5"))

      // No database change, we prepare it at first.
      prepare()

      "returns the plan when valid (fixed theater)" >> {
        val result = reservationController.reservedPlacesAsPlan(validTheaterFixed, validSessionFixed)
        (result must beRight[ResultSuccessEntity[ReservedSeats]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ReservedSeats]]].b.entity === expected)
      }

      "returns an error when theater is invalid" >> {
        val result = reservationController.reservedPlacesAsPlan(invalidTheater, validSessionFixed)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.undefinedTheater)
      }

      "returns an error when theater is dynamic" >> {
        val result = reservationController.reservedPlacesAsPlan(validTheaterDynamic, validSessionDynamic)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.notFixedTheater)
      }

      "returns an error when session is invalid" >> {
        val result = reservationController.reservedPlacesAsPlan(validTheaterFixed, invalidSession)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.undefinedSession)
      }
    }
  }
}
