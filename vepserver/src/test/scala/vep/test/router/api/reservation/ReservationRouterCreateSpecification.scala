package vep.test.router.api.reservation

import org.junit.runner.RunWith
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.session.{ReservationFormResult, ReservationFormBody, ReservationPriceForm}
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ReservationRouterCreateSpecification extends Specification with VepRouterDBInMemorySpecification with JsonMatchers {
  def prepare(): Unit = {
    prepareDB(
      "user/users-with-roles",
      "session/reservation-default"
    )
  }

  import InvalidReservationEntitiesImplicits._
  import spray.httpx.SprayJsonSupport._
  import vep.model.session.ReservationImplicits._

  sequential ^ "Specification of ReservationRouter" >> {
    "create" >> {
      lazy val validUrlFixed = "/reservation/existing-theater-fixed/existing-session_1"
      lazy val validEntityFixed = ReservationFormBody(
        firstName = "abc",
        lastName = "def",
        email = "abc@def.com",
        city = None,
        comment = None,
        seats = None,
        seatList = Seq("B1", "B2"),
        prices = Seq(ReservationPriceForm(price = 1, number = 2))
      )
      lazy val validEntityDynamic = ReservationFormBody(
        firstName = "abc",
        lastName = "def",
        email = "abc@def.com",
        city = None,
        comment = None,
        seats = Some(2),
        seatList = Seq(),
        prices = Seq(ReservationPriceForm(price = 3, number = 2))
      )
      lazy val validUrlDynamic = "/reservation/existing-theater-dynamic/existing-session_2"
      val validEntityFixedWithErrors = validEntityFixed.copy(firstName = "")
      val validEntityDynamicWithErrors = validEntityDynamic.copy(firstName = "")
      val invalidEntity = InvalidReservationFormBody("")

      "intercept a request to /reservation/<theater>/<session> as POST with valid entity (fixed theater)" >> {
        prepare()
        Post(validUrlFixed, validEntityFixed) ~> route ~> check {
          handled === true
        }
      }

      "intercept a request to /reservation/<theater>/<session> as POST with valid entity (dynamic theater)" >> {
        prepare()
        Post(validUrlDynamic, validEntityDynamic) ~> route ~> check {
          handled === true
        }
      }

      "refuse a request to /session/<theater>/<session> as POST when invalid entity" >> {
        prepare()
        Post(validUrlDynamic, invalidEntity) ~> route ~> check {
          handled === false
        }
      }

      "returns a code 400 with map when error(s) (fixed theater)" >> {
        prepare()
        Post(validUrlFixed, validEntityFixedWithErrors) ~> route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }

      "returns a code 400 with map when error(s) (dynamic theater)" >> {
        prepare()
        Post(validUrlDynamic, validEntityDynamicWithErrors) ~> route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }

      "return a code 200 with id and pass when success (fixed theater)" >> {
        prepare()
        Post(validUrlFixed, validEntityFixed) ~> route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[ReservationFormResult].id > 0) and
            (responseAs[ReservationFormResult].key.nonEmpty)
        }
      }

      "return a code 200 with canonical when success (dynamic theater)" >> {
        prepare()
        Post(validUrlDynamic, validEntityDynamic) ~> route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[ReservationFormResult].id > 0) and
            (responseAs[ReservationFormResult].key.nonEmpty)
        }
      }
    }
  }
}