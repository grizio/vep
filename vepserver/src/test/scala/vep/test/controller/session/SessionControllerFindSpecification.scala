package vep.test.controller.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.session.{SessionDetail, SessionPriceDetail}
import vep.service.AnormImplicits
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}
import vep.utils.DateUtils

@RunWith(classOf[JUnitRunner])
class SessionControllerFindSpecification extends Specification with VepMatcher with SessionMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
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
    "find should" >> {
      lazy val validTheater = "existing-theater"
      lazy val validSession = "existing-session_1"
      lazy val validPassedSession = "existing-session_2"
      lazy val invalidSession = "unknown-session"
      lazy val expectedSessionDetail = SessionDetail(
        theater = validTheater,
        canonical = validSession,
        date = DateUtils.toDateTime("2100-01-01 20:30:00"),
        name = "Existing session",
        reservationEndDate = DateUtils.toDateTime("2100-01-01 00:00:00"),
        prices = Seq(
          SessionPriceDetail(id = 1, name = "First price", price = 10, condition = None),
          SessionPriceDetail(id = 2, name = "Second price", price = 5, condition = Some("Children"))
        ),
        shows = Seq("existing-show")
      )
      lazy val expectedPassedSessionDetail = SessionDetail(
        theater = validTheater,
        canonical = validPassedSession,
        date = DateUtils.toDateTime("2000-01-01 20:30:00"),
        name = "Existing session",
        reservationEndDate = DateUtils.toDateTime("2000-01-01 00:00:00"),
        prices = Seq(
          SessionPriceDetail(id = 3, name = "First price", price = 10, condition = None)
        ),
        shows = Seq("existing-show")
      )

      // No database change, we prepare it at first.
      prepare()

      "returns the session when valid" >> {
        val sessionDetail = sessionController.find(validTheater, validSession)
        (sessionDetail.entity must beSome[SessionDetail]) and (sessionDetail.entity.get === expectedSessionDetail)
      }

      "returns the session when passed" >> {
        val sessionDetail = sessionController.find(validTheater, validPassedSession)
        (sessionDetail.entity must beSome[SessionDetail]) and (sessionDetail.entity.get === expectedPassedSessionDetail)
      }

      "returns nothing when session is invalid" >> {
        val sessionDetail = sessionController.find(validTheater, invalidSession)
        sessionDetail.entity must beNone
      }
    }
  }
}
