package vep.test.controller.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common._
import vep.model.session._
import vep.service.AnormImplicits
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}
import vep.test.utils.DateUtilsTest
import vep.utils.DateUtils

@RunWith(classOf[JUnitRunner])
class SessionControllerUpdateSpecification extends Specification with VepMatcher with SessionMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/session-theater",
      "session/session-company",
      "session/session-show",
      "session/session-default"
    )
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of SessionController" >> {
    "update should" >> {
      lazy val existingSession = SessionUpdateForm(
        theater = "existing-theater",
        session = "existing-session_1",
        date = "2100-01-01T20:30:00",
        name = "Existing session",
        reservationEndDate = "2100-01-01T00:00:00",
        reason = "",
        prices = Seq(
          SessionPriceForm(
            name = "First price",
            price = 10,
            condition = None
          ),
          SessionPriceForm(
            name = "Second price",
            price = 5,
            condition = Some("Children")
          )
        ),
        shows = Seq("existing-show")
      )

      lazy val validUpdatedSession = existingSession.copy(
        date = "2099-01-01T20:30:00",
        name = "Updated session",
        reservationEndDate = "2099-01-01T00:00:00",
        reason = "Mistake",
        prices = existingSession.prices map { p => p.copy(p.name + " updated", p.price * 2, p.condition map { c => c + " updated" }) },
        shows = existingSession.shows :+ "another-show"
      )

      lazy val invalidUpdatedSession = existingSession.copy(
        date = "2099-01-0120:30:00",
        name = "",
        reservationEndDate = "2099-01-0100:00:00",
        reason = "",
        prices = Seq(),
        shows = Seq()
      )

      lazy val invalidPrices = Seq(
        SessionPriceForm(name = "", price = -1, condition = None),
        SessionPriceForm(name = "a" * 251, price = -1, condition = Some("a" * 251))
      )

      "updates an existing entry with same values as given one and return a success on valid entity" >> {
        prepare()
        val result = sessionController.update(validUpdatedSession)
        val sessionOpt = sessionService.findDetail(validUpdatedSession.theater, validUpdatedSession.session)
        (result must beAnInstanceOf[Right[_, ResultSuccess]]) and
          (sessionOpt must beSome[SessionDetail]) and
          (sessionOpt.get must matchSessionDetail(validUpdatedSession))
      }

      "do not update an existing entry and return error on invalid entity" >> {
        prepare()
        val sessionBefore = sessionService.findDetail(invalidUpdatedSession.theater, invalidUpdatedSession.session)
        val result = sessionController.update(invalidUpdatedSession)
        val sessionAfter = sessionService.findDetail(invalidUpdatedSession.theater, invalidUpdatedSession.session)

        (result must beAnInstanceOf[Left[ResultStructuredErrors, _]]) and (sessionAfter === sessionBefore)
      }

      "indicates an error about a field value" >> {
        "when empty theater" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(theater = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("theater" -> ErrorCodes.emptyField))
        }

        "when empty date" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(date = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.emptyField))
        }

        "when invalid date (ISO format)" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(date = "20150101120000"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.invalidDate))
        }

        "when invalid date (impossible date)" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(date = "2015-30-01T00:00:00"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.invalidDate))
        }

        "when date different and too soon" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(date = DateUtilsTest.nowMinusOneMinuteISO, reservationEndDate = DateUtilsTest.nowMinusOneMinuteISO))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.dateTooSoon))
        }

        "when empty end reservation date" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(reservationEndDate = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.emptyField))
        }

        "when invalid end reservation date (ISO format)" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(reservationEndDate = "20150101120000"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.invalidDate))
        }

        "when invalid end reservation date (impossible date)" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(reservationEndDate = "2015-30-01T00:00:00"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.invalidDate))
        }

        "when end reservation date different and too soon" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(reservationEndDate = DateUtilsTest.nowMinusOneMinuteISO))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.dateTooSoon))
        }

        "when end reservation date too late" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(reservationEndDate = DateUtils.toStringISO(validUpdatedSession.dtDate.plusDays(1))))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.reservationEndDateTooLate))
        }

        "when empty name" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(name = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("name" -> ErrorCodes.emptyField))
        }

        "when big name" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(name = "a" * 251))
          result must matchPartialResultStructuredErrors(ErrorItem.build("name" -> ErrorCodes.bigString))
        }

        "when no price" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(prices = Seq()))
          result must matchPartialResultStructuredErrors(ErrorItem.build("prices" -> ErrorCodes.emptyField))
        }

        "when errors in prices" >> {
          "First item with empty name" >> {
            prepare()
            val result = sessionController.update(validUpdatedSession.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              0 -> ErrorItem.build("name" -> ErrorCodes.emptyField)))
            )))
          }

          "First item with negative price" >> {
            prepare()
            val result = sessionController.update(validUpdatedSession.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              0 -> ErrorItem.build("price" -> ErrorCodes.negative)))
            )))
          }

          "Second item with big name" >> {
            prepare()
            val result = sessionController.update(validUpdatedSession.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              1 -> ErrorItem.build("name" -> ErrorCodes.bigString)))
            )))
          }

          "Second item with negative price" >> {
            prepare()
            val result = sessionController.update(validUpdatedSession.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              1 -> ErrorItem.build("price" -> ErrorCodes.negative)))
            )))
          }

          "Second item with big condition" >> {
            prepare()
            val result = sessionController.update(validUpdatedSession.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              1 -> ErrorItem.build("condition" -> ErrorCodes.bigString)))
            )))
          }
        }

        "when no show" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(shows = Seq()))
          result must matchPartialResultStructuredErrors(ErrorItem.build("shows" -> ErrorCodes.emptyField))
        }
      }

      "indicates an error about a database constraint" >> {
        "when theater does not exists" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(theater = "unknown-theater"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("theater" -> ErrorCodes.undefinedTheater))
        }

        "when session does not exists" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(session = "unknown-session"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("session" -> ErrorCodes.undefinedSession))
        }

        "when show does not exists" >> {
          prepare()
          val result = sessionController.update(validUpdatedSession.copy(shows = Seq("unknown-show", "unknown-show-2")))
          result must matchPartialResultStructuredErrors(ErrorItemTree(Map(
            "shows" -> ErrorItemSeq(Map(
              0 -> ErrorItemFinal(Seq(ErrorCodes.undefinedShow)),
              1 -> ErrorItemFinal(Seq(ErrorCodes.undefinedShow))
            ))
          )))
        }
      }
    }
  }
}
