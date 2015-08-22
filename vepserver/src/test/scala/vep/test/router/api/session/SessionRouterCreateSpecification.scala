package vep.test.router.api.session

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.session.{SessionPriceForm, SessionFormBody}
import vep.test.router.api.VepRouterDBInMemorySpecification
import vep.test.utils.DateUtilsTest
import vep.utils.StringUtils

@RunWith(classOf[JUnitRunner])
class SessionRouterCreateSpecification extends Specification with VepRouterDBInMemorySpecification {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/session-theater",
      "session/session-company",
      "session/session-show",
      "session/session-default"
    )
  }

  import InvalidSessionEntitiesImplicits._
  import spray.httpx.SprayJsonSupport._
  import vep.model.session.SessionImplicits._

  sequential ^ "Specification of SessionRouter" >> {
    "create" >> {
      lazy val validUrl = "/session/existing-theater"
      lazy val validEntity = SessionFormBody(
        date = DateUtilsTest.nowPlusTwoDaysISO,
        reservationEndDate = DateUtilsTest.nowPlusOneDayISO,
        name = "My new session",
        prices = Seq(SessionPriceForm("Full price", 500, None), SessionPriceForm("Half price", 250, Some("Children"))),
        shows = Seq("existing-show", "another-show")
      )
      val validEntityWithErrors = validEntity.copy(name = "")
      val invalidEntity = InvalidSessionFormBody("")

      "intercept a request to /session/<theater> as POST with valid entity" >> {
        prepare()
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }

      "refuse a request to /session/<theater> as POST when invalid entity" >> {
        prepare()
        Post(validUrl, invalidEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === false
        }
      }

      "returns a code 401 when not authenticated" >> {
        prepare()
        Post(validUrl, validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }

      "returns a code 403 when authenticated but not authorized" >> {
        prepare()
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }

      "returns a code 400 with map when error(s)" >> {
        prepare()
        Post(validUrl, validEntityWithErrors) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }

      "return a code 200 with canonical when success" >> {
        prepare()
        Post(validUrl, validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] === StringUtils.canonicalize(validEntity.name) + "_1")
        }
      }
    }
  }
}