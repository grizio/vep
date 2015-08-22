package vep.test.controller.session

import anorm.SqlParser._
import anorm._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common._
import vep.model.session._
import vep.service.AnormImplicits
import vep.service.session.SessionParsers
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}
import vep.test.utils.DateUtilsTest
import vep.utils.{DB, DateUtils}

@RunWith(classOf[JUnitRunner])
class SessionControllerCreateSpecification extends Specification with VepMatcher with SessionMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
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
    "create should" >> {
      lazy val validDate = DateUtilsTest.nowPlusTwoDays
      lazy val validEndReservationDate = DateUtilsTest.nowPlusOneDay
      lazy val price1 = SessionPriceForm("Full price", 500, None)
      lazy val price2 = SessionPriceForm("Half price", 250, Some("Children"))
      lazy val show1 = "existing-show"
      lazy val show2 = "another-show"
      def expectedPriceList(sessionID: Int) = Seq(
        SessionPrice(0, sessionID, price1.name, price1.price, price1.condition),
        SessionPrice(0, sessionID, price2.name, price2.price, price2.condition)
      )
      def expectedShowSeq(sessionID: Int) = Seq(
        SessionShow(sessionID, 1, 1),
        SessionShow(sessionID, 2, 2)
      )

      lazy val validSessionForm = SessionForm(
        theater = "existing-theater",
        date = DateUtils.toStringISO(validDate),
        reservationEndDate = DateUtils.toStringISO(validEndReservationDate),
        name = "My new session",
        prices = Seq(price1, price2),
        shows = Seq(show1, show2)
      )
      lazy val existingSessionForm = validSessionForm.copy(date = "2100-01-01T20:30")
      lazy val invalidSessionForm = SessionForm(
        theater = "undefined-theater",
        date = "2015-01-0220:30",
        reservationEndDate = "2015-02-30T00:00",
        name = "",
        prices = Seq(), shows = Seq()
      )
      lazy val invalidPrices = Seq(
        SessionPriceForm(name = "", price = -1, condition = None),
        SessionPriceForm(name = "a" * 251, price = -1, condition = Some("a" * 251))
      )

      def countSessions = DB.withConnection { implicit c => SQL("SELECT count(*) FROM session").as(scalar[Int].single) }

      "create a new entry with same values as given one and return a success on valid entity" >> {
        prepare()
        val result = sessionController.create(validSessionForm)
        val sessionOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM session WHERE theater = {theater} AND date = {date}")
            .on("theater" -> 1)
            .on("date" -> DateUtils.toStringSQL(validDate))
            .as(SessionParsers.session.singleOpt)
        }

        lazy val sessionPriceList = DB.withConnection { implicit c =>
          SQL("SELECT * FROM session_price WHERE session = {session} ORDER BY id ASC")
            .on("session" -> sessionOpt.get.id)
            .as(SessionParsers.sessionPrice *)
        }

        lazy val sessionShowList = DB.withConnection { implicit c =>
          SQL("SELECT * FROM session_show WHERE session = {session} ORDER BY num ASC")
            .on("session" -> sessionOpt.get.id)
            .as(SessionParsers.sessionShow *)
        }

        (result must beAnInstanceOf[Right[_, ResultSuccess]]) and
          (sessionOpt must beSome[Session]) and
          matchSession(sessionOpt.get, Session(0, 1, "", validDate, validSessionForm.name, validEndReservationDate)) and
          matchSessionPriceSeq(sessionPriceList, expectedPriceList(sessionOpt.get.id)) and
          matchSessionShowSeq(sessionShowList, expectedShowSeq(sessionOpt.get.id))
      }

      "do not create a new entry and return error on invalid entity" >> {
        prepare()
        val nbSessionsStart = countSessions
        val result = sessionController.create(invalidSessionForm)
        val nbSessionsEnd = countSessions

        (result must beAnInstanceOf[Left[ResultStructuredErrors, _]]) and (nbSessionsEnd === nbSessionsStart)
      }

      "indicates an error about a field value" >> {
        "when empty theater" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(theater = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("theater" -> ErrorCodes.emptyField))
        }

        "when empty date" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(date = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.emptyField))
        }

        "when invalid date (ISO format)" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(date = "20150101120000"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.invalidDate))
        }

        "when invalid date (impossible date)" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(date = "2015-30-01T00:00:00"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.invalidDate))
        }

        "when date too soon" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(date = DateUtilsTest.nowMinusOneMinuteISO))
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.dateTooSoon))
        }

        "when empty end reservation date" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(reservationEndDate = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.emptyField))
        }

        "when invalid end reservation date (ISO format)" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(reservationEndDate = "20150101120000"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.invalidDate))
        }

        "when invalid end reservation date (impossible date)" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(reservationEndDate = "2015-30-01T00:00:00"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.invalidDate))
        }

        "when end reservation date too soon" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(reservationEndDate = DateUtilsTest.nowMinusOneMinuteISO))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.dateTooSoon))
        }

        "when end reservation date too late" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(date = DateUtilsTest.nowPlusOneDayISO, reservationEndDate = DateUtilsTest.nowPlusTwoDaysISO))
          result must matchPartialResultStructuredErrors(ErrorItem.build("reservationEndDate" -> ErrorCodes.reservationEndDateTooLate))
        }

        "when empty name" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(name = ""))
          result must matchPartialResultStructuredErrors(ErrorItem.build("name" -> ErrorCodes.emptyField))
        }

        "when big name" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(name = "a" * 251))
          result must matchPartialResultStructuredErrors(ErrorItem.build("name" -> ErrorCodes.bigString))
        }

        "when no price" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(prices = Seq()))
          result must matchPartialResultStructuredErrors(ErrorItem.build("prices" -> ErrorCodes.emptyField))
        }

        "when errors in prices" >> {
          "First item with empty name" >> {
            prepare()
            val result = sessionController.create(validSessionForm.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              0 -> ErrorItem.build("name" -> ErrorCodes.emptyField)))
            )))
          }

          "First item with negative price" >> {
            prepare()
            val result = sessionController.create(validSessionForm.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              0 -> ErrorItem.build("price" -> ErrorCodes.negative)))
            )))
          }

          "Second item with big name" >> {
            prepare()
            val result = sessionController.create(validSessionForm.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              1 -> ErrorItem.build("name" -> ErrorCodes.bigString)))
            )))
          }

          "Second item with negative price" >> {
            prepare()
            val result = sessionController.create(validSessionForm.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              1 -> ErrorItem.build("price" -> ErrorCodes.negative)))
            )))
          }

          "Second item with big condition" >> {
            prepare()
            val result = sessionController.create(validSessionForm.copy(prices = invalidPrices))
            result must matchPartialResultStructuredErrors(ErrorItemTree(Map("prices" -> ErrorItemSeq(Map(
              1 -> ErrorItem.build("condition" -> ErrorCodes.bigString)))
            )))
          }
        }

        "when no show" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(shows = Seq()))
          result must matchPartialResultStructuredErrors(ErrorItem.build("shows" -> ErrorCodes.emptyField))
        }
      }

      "indicates an error about a database constraint" >> {
        "when theater does not exists" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(theater = "unknown-theater"))
          result must matchPartialResultStructuredErrors(ErrorItem.build("theater" -> ErrorCodes.undefinedTheater))
        }

        "when show does not exists" >> {
          prepare()
          val result = sessionController.create(validSessionForm.copy(shows = Seq("unknown-show", "unknown-show-2")))
          result must matchPartialResultStructuredErrors(ErrorItemTree(Map(
            "shows" -> ErrorItemSeq(Map(
              0 -> ErrorItemFinal(Seq(ErrorCodes.undefinedShow)),
              1 -> ErrorItemFinal(Seq(ErrorCodes.undefinedShow))
            ))
          )))
        }

        "when duplicate session in a theater" >> {
          prepare()
          val result = sessionController.create(existingSessionForm)
          result must matchPartialResultStructuredErrors(ErrorItem.build("date" -> ErrorCodes.existingDateForTheater))
        }
      }
    }
  }
}
