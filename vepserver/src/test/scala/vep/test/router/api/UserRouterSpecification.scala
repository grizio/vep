package vep.test.router.api

import akka.actor.ActorRefFactory
import org.specs2.mutable.Specification
import spray.http._
import spray.json.DefaultJsonProtocol
import spray.testkit.Specs2RouteTest
import vep.model.user.UserRegistration
import vep.router.VepApiRouter
import vep.test.controller.VepControllersInMemoryComponent

case class InvalidUserRegistration(email: String, firstName: String)

object InvalidEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidUserRegistration = jsonFormat2(InvalidUserRegistration)
}

class UserRouterSpecification extends Specification with Specs2RouteTest with VepApiRouter with VepControllersInMemoryComponent {
  override def actorRefFactory: ActorRefFactory = system

  import InvalidEntitiesImplicits._
  import spray.httpx.SprayJsonSupport._
  import vep.model.user.UserImplicits._

  "Specification of UserRouter" >> {
    "register" >> {
      "intercept a request to /user/register as POST with valid entity" >> {
        Post("/user/register", UserRegistration("", "", "", "", None)) ~> route ~> check {
          handled === true
        }
      }
      "refuse a request to /user/register as GET" >> {
        Get("/user/register") ~> route ~> check {
          handled === false
        }
      }
      "refuse a request to /user/register as POST when invalid entity" >> {
        Post("/user/register", InvalidUserRegistration("", "")) ~> route ~> check {
          handled === false
        }
      }
      "returns a code 400 with map when error(s)" >> {
        Post("/user/register", UserRegistration("", "", "", "", None)) ~> route ~> check {
          (status === StatusCodes.BadRequest) and
            (responseAs[String] must startWith("{"))
        }
      }
      "return a code 200 with nothing when success" >> {
        Post("/user/register", UserRegistration("a.valid@email.com", "firs name", "last name", "passw0rd", None)) ~> route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] must beEmpty)
        }
      }
    }
  }
}
