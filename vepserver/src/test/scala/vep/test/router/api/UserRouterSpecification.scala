package vep.test.router.api

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http._
import spray.json.DefaultJsonProtocol
import vep.model.common.Roles
import vep.model.user.{RolesSeq, UserLogin, UserRegistration}

case class InvalidUserRegistration(email: String, firstName: String)

case class InvalidUserLogin(email: String)

object InvalidUserEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidUserRegistration = jsonFormat2(InvalidUserRegistration)
  implicit val impInvalidUserLogin = jsonFormat1(InvalidUserLogin)
}

@RunWith(classOf[JUnitRunner])
class UserRouterSpecification extends Specification with VepRouterSpecification {

  import InvalidUserEntitiesImplicits._
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
            (responseAs[String] === "null")
        }
      }
    }

    "login" >> {
      "intercept a request to /login as POST with valid entity" >> {
        Post("/login", UserLogin("", "")) ~> route ~> check {
          handled === true
        }
      }
      "refuse a request to /login as GET" >> {
        Get("/login") ~> route ~> check {
          handled === false
        }
      }
      "refuse a request to /login as POST when invalid entity" >> {
        Post("/login", InvalidUserLogin("")) ~> route ~> check {
          handled === false
        }
      }
      "returns a code 403 with int when error" >> {
        Post("/login", UserLogin("an@unknown.user", "an unknown password")) ~> route ~> check {
          (status === StatusCodes.Forbidden) and
            (responseAs[String] must beMatching("[0-9+]"))
        }
      }
      "return a code 200 with a string when success" >> {
        Post("/user/register", UserRegistration("a.valid@email.com", "firs name", "last name", "passw0rd", None)) ~> route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] must not beEmpty)
        }
      }
    }

    "get roles" >> {
      "intercept a request to /user/roles as GET with valid credentials" >> {
        Get("/user/roles") ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /user/roles as POST" >> {
        Post("/user/roles") ~> route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        Get("/user/roles") ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "return a code 200 with a list of string (json) when success" >> {
        Get("/user/roles") ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          (status === StatusCodes.OK) and
            (responseAs[String] must not beEmpty) and
            (responseAs[String] must beMatching("^\\[(\"[^\"]+\",?)*\\]$"))
        }
      }
    }

    "update roles" >> {
      val validEntity = RolesSeq(Seq(Roles.user, Roles.userManager))
      val invalidEntity = InvalidUserLogin("")
      val validEntityInvalidData = RolesSeq(Seq("unknown"))
      "intercept a request to /user/roles/1 as POST with valid credentials" >> {
        Post("/user/roles/1", validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /user/roles/1 as Get" >> {
        Get("/user/roles/1") ~> route ~> check {
          handled === false
        }
      }
      "refuse a request to /user/roles/1 as POST when invalid entity" >> {
        Post("/user/roles/1", invalidEntity) ~> route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        Post("/user/roles/1", validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "returns a code 403 when authenticated but not authorized" >> {
        Post("/user/roles/1", validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }
      "returns a code 400 when authenticated, authorized but invalid data" >> {
        Post("/user/roles/1", validEntityInvalidData) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          status === StatusCodes.BadRequest
        }
      }
      "returns a code 404 when authenticated, authorized but unknown user id" >> {
        Post("/user/roles/1000000", validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          status === StatusCodes.NotFound
        }
      }
      "return a code 200 when success" >> {
        Post("/user/roles/1", validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }
    }

    "get list of users" >> {
      val validEntity = RolesSeq(Seq(Roles.user, Roles.userManager))
      val invalidEntity = InvalidUserLogin("")

      "intercept a request to /user/list as GET with valid credentials" >> {
        Get("/user/list", validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          handled === true
        }
      }
      "refuse a request to /user/list as POST" >> {
        Post("/user/list") ~> route ~> check {
          handled === false
        }
      }
      "returns a code 401 when not authenticated" >> {
        Get("/user/list", validEntity) ~> route ~> check {
          status === StatusCodes.Unauthorized
        }
      }
      "returns a code 403 when authenticated but not authorized" >> {
        Get("/user/list", validEntity) ~>
          addCredentials(validCredentialsUser) ~>
          route ~> check {
          status === StatusCodes.Forbidden
        }
      }
      "return a code 200 when success" >> {
        Get("/user/list", validEntity) ~>
          addCredentials(validCredentialsAdmin) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }
    }
  }
}
