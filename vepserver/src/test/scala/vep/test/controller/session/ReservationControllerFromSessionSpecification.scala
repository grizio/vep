package vep.test.controller.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultError, ResultSuccessEntity}
import vep.model.session.{ReservationDetail, ReservationDetailSeq, ReservationPriceDetail, SessionDetail}
import vep.service.AnormImplicits
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}

@RunWith(classOf[JUnitRunner])
class ReservationControllerFromSessionSpecification extends Specification with VepMatcher with ReservationMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of ReservationController" >> {
    "fromSession should" >> {
      val validTheaterFixed = "existing-theater-fixed"
      val validSessionFixed = "existing-session_1"
      val validTheaterDynamic = "existing-theater-dynamic"
      val validSessionDynamic = "existing-session_2"
      val invalidTheater = "undefined-theater"
      val invalidSession = "undefined-session"

      lazy val expectedFixed = ReservationDetailSeq(Seq(
        ReservationDetail(
          id = 1,
          session = validSessionFixed,
          firstName = "John",
          lastName = "Smith",
          email = "abc@def.com",
          city = None,
          comment = None,
          seats = None,
          pass = "123456789",
          seatList = Seq("A1", "A2", "A3", "A4"),
          prices = Seq(ReservationPriceDetail(
            price = 1,
            number = 1,
            value = 10
          ), ReservationPriceDetail(
            price = 2,
            number = 3,
            value = 5
          ))
        ),
        ReservationDetail(
          id = 3,
          session = validSessionFixed,
          firstName = "Al",
          lastName = "Zheimer",
          email = "al@zheimer.undefined.com",
          city = None,
          comment = None,
          seats = None,
          pass = "246813579",
          seatList = Seq("A5"),
          prices = Seq(ReservationPriceDetail(
            price = 1,
            number = 1,
            value = 10
          ))
        )
      ))
      lazy val expectedDynamic = ReservationDetailSeq(Seq(
        ReservationDetail(
          id = 2,
          session = validSessionDynamic,
          firstName = "Albert",
          lastName = "Einstein",
          email = "ghi@jkl.com",
          city = None,
          comment = None,
          seats = Some(5),
          pass = "987654321",
          seatList = Seq(),
          prices = Seq(ReservationPriceDetail(
            price = 3,
            number = 2,
            value = 12
          ), ReservationPriceDetail(
            price = 4,
            number = 3,
            value = 6
          ))
        ),
        ReservationDetail(
          id = 4,
          session = validSessionDynamic,
          firstName = "Miss",
          lastName = "Tyc",
          email = "miss@tyc.undefined.com",
          city = None,
          comment = None,
          seats = Some(1),
          pass = "987654321",
          seatList = Seq(),
          prices = Seq(ReservationPriceDetail(
            price = 3,
            number = 1,
            value = 12
          ))
        )
      ))

      // No database change, we prepare it at first.
      prepare()

      "returns the reservation list when valid (fixed theater)" >> {
        val result = reservationController.fromSession(validTheaterFixed, validSessionFixed)
        (result must beRight[ResultSuccessEntity[ReservationDetailSeq]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ReservationDetailSeq]]].b.entity === expectedFixed)
      }

      "returns the reservation list when valid (dynamic theater)" >> {
        val result = reservationController.fromSession(validTheaterDynamic, validSessionDynamic)
        (result must beRight[ResultSuccessEntity[ReservationDetailSeq]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ReservationDetailSeq]]].b.entity === expectedDynamic)
      }

      "returns an error when theater is invalid" >> {
        val result = reservationController.fromSession(invalidTheater, validSessionFixed)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.undefinedTheater)
      }

      "returns an error when session is invalid" >> {
        val result = reservationController.fromSession(validTheaterFixed, invalidSession)
        (result must beLeft[ResultError]) and (result.asInstanceOf[Left[ResultError, _]].a.code === ErrorCodes.undefinedSession)
      }
    }
  }
}
