package vep.test.controller.session

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultErrors, ResultSuccessEntity}
import vep.model.session.{SessionSearch, SessionSearchResponse, SessionSearchResult, SessionSearchShow}
import vep.service.AnormImplicits
import vep.test.controller.{VepControllersDBInMemoryComponent, VepMatcher}

@RunWith(classOf[JUnitRunner])
class SessionControllerSearchSpecification extends Specification with VepMatcher with SessionMatcher with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/session-theater",
      "session/session-company",
      "session/session-show",
      "session/session-search"
    )
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of SessionController" >> {
    "search should" >> {
      lazy val defaultSessionSearch = SessionSearch(t = None, s = None, sd = None, ed = None, o = None, p = None)
      lazy val show1 = SessionSearchShow("existing-show", "Existing show")
      lazy val show2 = SessionSearchShow("another-show", "Another show")
      lazy val show3 = SessionSearchShow("this-show", "This show")
      lazy val show4 = SessionSearchShow("existing-show-2", "Existing show second part")
      lazy val shows143 = Seq(show1, show4, show3)
      lazy val shows14 = Seq(show1, show4)
      lazy val shows3 = Seq(show3)
      lazy val shows2 = Seq(show2)
      lazy val sessions = Map(
        2 -> SessionSearchResult("existing-session_2", "my-theater", "My theater", shows143, new DateTime("2100-01-02T20:30:00")),
        3 -> SessionSearchResult("existing-session_3", "existing-theater", "Existing theater", shows143, new DateTime("2100-01-03T20:30:00")),
        4 -> SessionSearchResult("existing-session_4", "my-theater", "My theater", shows143, new DateTime("2100-01-04T20:30:00")),
        5 -> SessionSearchResult("existing-session_5", "existing-theater", "Existing theater", shows143, new DateTime("2100-01-05T20:30:00")),
        6 -> SessionSearchResult("another-session_1", "my-theater", "My theater", shows14, new DateTime("2100-01-06T20:30:00")),
        7 -> SessionSearchResult("another-session_2", "existing-theater", "Existing theater", shows14, new DateTime("2100-01-07T20:30:00")),
        8 -> SessionSearchResult("another-session_3", "my-theater", "My theater", shows14, new DateTime("2100-01-08T20:30:00")),
        9 -> SessionSearchResult("another-session_4", "existing-theater", "Existing theater", shows14, new DateTime("2100-01-09T20:30:00")),
        10 -> SessionSearchResult("another-session_5", "my-theater", "My theater", shows14, new DateTime("2100-01-10T20:30:00")),
        11 -> SessionSearchResult("this-session_1", "existing-theater", "Existing theater", shows3, new DateTime("2100-02-01T15:30:00")),
        12 -> SessionSearchResult("this-session_2", "my-theater", "My theater", shows3, new DateTime("2100-02-01T16:30:00")),
        13 -> SessionSearchResult("this-session_3", "existing-theater", "Existing theater", shows3, new DateTime("2100-02-01T17:30:00")),
        14 -> SessionSearchResult("this-session_4", "my-theater", "My theater", shows3, new DateTime("2100-02-01T18:30:00")),
        15 -> SessionSearchResult("this-session_5", "existing-theater", "Existing theater", shows3, new DateTime("2100-02-01T19:30:00")),
        16 -> SessionSearchResult("this-session_6", "my-theater", "My theater", shows3, new DateTime("2100-03-01T20:00:00")),
        17 -> SessionSearchResult("this-session_7", "existing-theater", "Existing theater", shows3, new DateTime("2100-03-01T20:30:00")),
        18 -> SessionSearchResult("this-session_8", "my-theater", "My theater", shows3, new DateTime("2100-03-02T20:00:00")),
        19 -> SessionSearchResult("this-session_9", "existing-theater", "Existing theater", shows3, new DateTime("2100-03-02T20:30:00")),
        20 -> SessionSearchResult("this-session_10", "my-theater", "My theater", shows3, new DateTime("2100-03-03T20:30:00")),
        21 -> SessionSearchResult("final-session_1", "existing-theater", "Existing theater", shows2, new DateTime("2100-04-01T20:30:00")),
        22 -> SessionSearchResult("final-session_2", "my-theater", "My theater", shows2, new DateTime("2100-04-02T20:30:00")),
        23 -> SessionSearchResult("final-session_3", "existing-theater", "Existing theater", shows2, new DateTime("2100-04-03T20:30:00")),
        24 -> SessionSearchResult("final-session_4", "my-theater", "My theater", shows2, new DateTime("2100-04-04T20:30:00")),
        25 -> SessionSearchResult("final-session_5", "existing-theater", "Existing theater", shows2, new DateTime("2100-04-05T20:30:00")),
        26 -> SessionSearchResult("final-session_6", "my-theater", "My theater", shows2, new DateTime("2100-05-01T15:30:00")),
        27 -> SessionSearchResult("final-session_7", "existing-theater", "Existing theater", shows2, new DateTime("2100-05-01T16:30:00")),
        28 -> SessionSearchResult("final-session_8", "my-theater", "My theater", shows2, new DateTime("2100-05-01T17:30:00")),
        29 -> SessionSearchResult("final-session_9", "existing-theater", "Existing theater", shows2, new DateTime("2100-05-01T18:30:00"))
      )

      def buildExpectedSessions(id: Int*) = id.map(sessions.get(_).get).toList

      // One prepare for all the class because only queries
      prepare()

      "Returns the list of the first 20 entities ordered by date in future when no criteria" >> {
        val result = sessionController.search(defaultSessionSearch)
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(2 to 21: _*),
          pageMax = 2
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of the last 8 entities ordered by date in future when page 2" >> {
        val result = sessionController.search(defaultSessionSearch.copy(p = Some(2)))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(22 to 29: _*),
          pageMax = 2
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 14 entities ordered by date in future when theater = 'my-theater'" >> {
        val result = sessionController.search(defaultSessionSearch.copy(t = Some("my-theater")))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 19 entities ordered by date in future when show = 'is'" >> {
        val result = sessionController.search(defaultSessionSearch.copy(s = Some("is")))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(2 to 20: _*),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 4 entities after 30/04/2100 ordered by date in future when start date = '30/04/2100'" >> {
        val result = sessionController.search(defaultSessionSearch.copy(sd = Some("2100-04-30T00:00:00")))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(26, 27, 28, 29),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 9 entities before 01/02/2100 ordered by date in future when end date = '01/02/2100'" >> {
        val result = sessionController.search(defaultSessionSearch.copy(ed = Some("2100-02-01T00:00:00")))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(2 to 10: _*),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 4 entities between 01/01/2015 and 01/05/2015 ordered by date in future when start date = '01/01/2100' and end date = '05/01/2100' included" >> {
        val result = sessionController.search(defaultSessionSearch.copy(
          sd = Some("2100-01-01T00:00:00"),
          ed = Some("2100-01-05T23:59:59")
        ))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(2 to 5: _*),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 20 first entities ordered by theater when order = 't'" >> {
        val result = sessionController.search(defaultSessionSearch.copy(o = Some("t")))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 2, 4, 6, 8, 10, 12),
          pageMax = 2
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 20 first entities ordered by show when order = 's'" >> {
        val result = sessionController.search(defaultSessionSearch.copy(o = Some("s")))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(21, 22, 23, 24, 25, 26, 27, 28, 29, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
          pageMax = 2
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of 20 first entities ordered by date when order = 'd'" >> {
        val result = sessionController.search(defaultSessionSearch.copy(o = Some("d")))
        val expected = SessionSearchResponse(
          sessions = buildExpectedSessions(2 to 21: _*),
          pageMax = 2
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[SessionSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns an error when page < 0" >> {
        val result = sessionController.search(defaultSessionSearch.copy(p = Some(-1)))
        val expected = ResultErrors(Map("p" -> Seq(ErrorCodes.negativeOrNull)))

        (result must beAnInstanceOf[Left[ResultErrors, _]]) and
          (result.asInstanceOf[Left[ResultErrors, _]].a must beEqualTo(expected))
      }

      "Returns an error when page = 0" >> {
        val result = sessionController.search(defaultSessionSearch.copy(p = Some(0)))
        val expected = ResultErrors(Map("p" -> Seq(ErrorCodes.negativeOrNull)))

        (result must beAnInstanceOf[Left[ResultErrors, _]]) and
          (result.asInstanceOf[Left[ResultErrors, _]].a must beEqualTo(expected))
      }

      "Returns an error when unknown order column" >> {
        val result = sessionController.search(defaultSessionSearch.copy(o = Some("unknown")))
        val expected = ResultErrors(Map("o" -> Seq(ErrorCodes.unknownOrder)))

        (result must beAnInstanceOf[Left[ResultErrors, _]]) and
          (result.asInstanceOf[Left[ResultErrors, _]].a must beEqualTo(expected))
      }

      "Returns an error when invalid start date" >> {
        val result = sessionController.search(defaultSessionSearch.copy(sd = Some("20000101000000")))
        val expected = ResultErrors(Map("sd" -> Seq(ErrorCodes.invalidDate)))

        (result must beAnInstanceOf[Left[ResultErrors, _]]) and
          (result.asInstanceOf[Left[ResultErrors, _]].a must beEqualTo(expected))
      }

      "Returns an error when invalid end date" >> {
        val result = sessionController.search(defaultSessionSearch.copy(ed = Some("20000101000000")))
        val expected = ResultErrors(Map("ed" -> Seq(ErrorCodes.invalidDate)))

        (result must beAnInstanceOf[Left[ResultErrors, _]]) and
          (result.asInstanceOf[Left[ResultErrors, _]].a must beEqualTo(expected))
      }
    }
  }
}
