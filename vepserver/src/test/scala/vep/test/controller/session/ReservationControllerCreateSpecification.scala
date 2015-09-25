package vep.test.controller.session

import anorm.SqlParser._
import anorm._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common._
import vep.model.session._
import vep.service.AnormImplicits
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}
import vep.utils.DB

@RunWith(classOf[JUnitRunner])
class ReservationControllerCreateSpecification extends Specification with VepMatcher with ReservationMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of ReservationController" >> {
    "create should" >> {
      lazy val validBaseReservationForm = ReservationForm(
        theater = "",
        session = "",
        firstName = "John",
        lastName = "Smith",
        email = "abc@def.com",
        city = Some("Paris, France"),
        comment = Some("A comment for my reservation"),
        seats = None,
        seatList = Seq(),
        prices = Seq()
      )
      lazy val validFixedReservationForm = validBaseReservationForm.copy(
        theater = "existing-theater-fixed",
        session = "existing-session_1",
        seatList = Seq("B1", "B2", "B3", "B4", "B5"),
        prices = Seq(
          ReservationPriceForm(price = 1, number = 2),
          ReservationPriceForm(price = 2, number = 3)
        )
      )
      lazy val validDynamicReservationForm = validBaseReservationForm.copy(
        theater = "existing-theater-dynamic",
        session = "existing-session_2",
        seats = Some(5),
        prices = Seq(
          ReservationPriceForm(price = 3, number = 2),
          ReservationPriceForm(price = 4, number = 3)
        )
      )

      def countReservation = DB.withConnection { implicit c => SQL("SELECT count(*) FROM reservation").as(scalar[Int].single) }

      "create a new entry with same values as given one and return a success on valid entity (fixed theater)" >> {
        prepare()
        val result = reservationController.create(validFixedReservationForm)
        val idOpt = result.fold(
          error => None,
          success => Some(success.entity.id)
        )
        val reservationOpt = idOpt.flatMap(reservationService.find)

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ReservationFormResult]]]) and
          (reservationOpt must beSome[ReservationDetail]) and
          (reservationOpt.get must matchReservationDetail(validFixedReservationForm))
      }

      "create a new entry with same values as given one and return a success on valid entity (dynamic theater)" >> {
        prepare()
        val result = reservationController.create(validDynamicReservationForm)
        val idOpt = result.fold(
          error => None,
          success => Some(success.entity.id)
        )
        val reservationOpt = idOpt.flatMap(reservationService.find)

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ReservationFormResult]]]) and
          (reservationOpt must beSome[ReservationDetail]) and
          (reservationOpt.get must matchReservationDetail(validDynamicReservationForm))
      }

      "do not create a new entry and return error on invalid entity" >> {
        prepare()
        val nbReservationsStart = countReservation
        val result = reservationController.create(validBaseReservationForm.copy(theater = ""))
        val nbReservationsEnd = countReservation

        (result must beAnInstanceOf[Left[ResultStructuredErrors, _]]) and (nbReservationsEnd === nbReservationsStart)
      }

      "Indicate an error about field value (both fixed and dynamic theater)" >> {
        // We can use dynamic or fixed form as base because these fields should be checked in both cases.

        "when empty theater" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(theater = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("theater" -> ErrorCodes.emptyField))
        }

        "when empty session" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(session = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("session" -> ErrorCodes.emptyField))
        }

        "when undefined session" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(session = "undefined"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("session" -> ErrorCodes.undefinedSession))
        }

        "when empty first name" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(firstName = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("firstName" -> ErrorCodes.emptyField))
        }

        "when first name too big" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(firstName = "a" * 251))
          result must matchPartialResultStructuredErrors(ErrorItem.build("firstName" -> ErrorCodes.bigString))
        }

        "when empty last name" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(lastName = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("lastName" -> ErrorCodes.emptyField))
        }

        "when last name too big" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(lastName = "a" * 251))
          result must matchPartialResultStructuredErrors(ErrorItem.build("lastName" -> ErrorCodes.bigString))
        }

        "when empty email" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(email = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("email" -> ErrorCodes.emptyField))
        }

        "when email too big" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(email = "a" * 251 + "@aui.com"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("email" -> ErrorCodes.bigString))
        }

        "when invalid email" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(email = "abc@def"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("email" -> ErrorCodes.invalidEmail))
        }

        "when city too big" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(city = Some("a" * 251)))
          result must matchPartialResultStructuredErrors(ErrorItem.build("city" -> ErrorCodes.bigString))
        }

        "when prices empty" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(prices = Seq()))
          result must matchPartialResultStructuredErrors(ErrorItem.build("prices" -> ErrorCodes.emptyField))
        }

        "cases of dynamic theater" >> {
          // When empty seat impossible because prices is consider empty or different of 0

          "when seats negative" >> {
            prepare()
            val result = reservationController.create(validDynamicReservationForm.copy(seats = Some(-1)))
            result must matchPartialResultStructuredErrors(ErrorItem.build("seats" -> ErrorCodes.negativeOrNull))
          }

          "when seats null" >> {
            prepare()
            val result = reservationController.create(validDynamicReservationForm.copy(seats = Some(0)))
            result must matchPartialResultStructuredErrors(ErrorItem.build("seats" -> ErrorCodes.negativeOrNull))
          }

          "when seats number != price number" >> {
            prepare()
            val result = reservationController.create(validDynamicReservationForm.copy(seats = Some(4)))
            result must matchPartialResultStructuredErrors(ErrorItem.build("prices" -> ErrorCodes.differentSeatPriceNumbers))
          }
        }

        "cases of fixed theater" >> {
          // When empty seat impossible because prices is consider empty or different of 0

          "when seat with empty code (first)" >> {
            prepare()
            val result = reservationController.create(validFixedReservationForm.copy(seatList = Seq("", "B1", "B2", "B3", "B4")))
            result must matchPartialResultStructuredErrors(ErrorItemTree("seatList" -> ErrorItemSeq(
              0 -> ErrorItemFinal(Seq(ErrorCodes.emptyField))
            )))
          }

          "when seat with empty code (second)" >> {
            prepare()
            val result = reservationController.create(validFixedReservationForm.copy(seatList = Seq("B1", "", "B2", "B3", "B4")))
            result must matchPartialResultStructuredErrors(ErrorItemTree("seatList" -> ErrorItemSeq(
              1 -> ErrorItemFinal(Seq(ErrorCodes.emptyField))
            )))
          }

          "when seats number != price number" >> {
            prepare()
            val result = reservationController.create(validFixedReservationForm.copy(seatList = Seq("B1", "B2", "B3", "B4")))
            result must matchPartialResultStructuredErrors(ErrorItem.build("prices" -> ErrorCodes.differentSeatPriceNumbers))
          }
        }
      }

      "Indicate an error about field value (both fixed and dynamic theater)" >> {
        "when session does not exists" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(session = "unknown"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("session" -> ErrorCodes.undefinedSession))
        }

        "when price does not exists for given session" >> {
          prepare()
          val result = reservationController.create(validDynamicReservationForm.copy(prices = Seq(ReservationPriceForm(10, 5))))
          result must matchPartialResultStructuredErrors(ErrorItemTree(
            "prices" -> ErrorItemSeq(
              0 -> ErrorItemFinal(Seq(ErrorCodes.undefinedPrice))
            )
          ))
        }

        "cases of fixed theater" >> {
          "when seat does not exists" >> {
            prepare()
            val result = reservationController.create(validFixedReservationForm.copy(seatList = Seq("undefined", "B1", "B2", "B3", "B4")))
            result must matchPartialResultStructuredErrors(ErrorItemTree("seatList" -> ErrorItemSeq(
              0 -> ErrorItemFinal(Seq(ErrorCodes.undefinedSeat))
            )))
          }

          "when seat is taken" >> {
            prepare()
            val result = reservationController.create(validFixedReservationForm.copy(seatList = Seq("A1", "B1", "B2", "B3", "B4")))
            result must matchPartialResultStructuredErrors(ErrorItemTree("seatList" -> ErrorItemSeq(
              0 -> ErrorItemFinal(Seq(ErrorCodes.reservedSeat))
            )))
          }
        }
      }
    }
  }
}
