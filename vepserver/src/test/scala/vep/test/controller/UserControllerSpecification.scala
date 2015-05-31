package vep.test.controller

import org.specs2.mutable.Specification
import spray.http.DateTime
import vep.controller.UserControllerProductionComponent
import vep.model.common._
import vep.model.user.{User, UserLogin, UserRegistration}
import vep.test.service.inmemory.VepServicesInMemoryComponent

class UserControllerForSpecificationComponent
  extends UserControllerProductionComponent
  with VepServicesInMemoryComponent

class UserControllerSpecification extends Specification {
  "Specifications of UserController" >> {
    "register should" >> {
      "register a valid entity" >> {
        val ucComp = new UserControllerForSpecificationComponent
        val userRegistration = UserRegistration("abc.abc@abc.com", "first name", "last name", "passw0rd", None)

        val result = ucComp.userController.register(userRegistration)

        result must beAnInstanceOf[Right[_, ResultSuccess]]
      }

      "refuse an entity with invalid fields" >> {
        val ucComp = new UserControllerForSpecificationComponent
        val userRegistration = UserRegistration("", "", "", "", None)

        val result = ucComp.userController.register(userRegistration)

        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "indicates an error about a field value" >> {
        "when empty email" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("email")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("email").get must contain(ErrorCodes.emptyEmail))
        }

        "when invalid email" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("abc.abc", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("email")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("email").get must contain(ErrorCodes.invalidEmail))
        }

        "when empty first name" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("firstName")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("firstName").get must contain(ErrorCodes.emptyFirstName))
        }

        "when empty last name" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("lastName")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("lastName").get must contain(ErrorCodes.emptyLastName))
        }

        "when empty password" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("password")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("password").get must contain(ErrorCodes.emptyPassword))
        }

        "when not secured password" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "password", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("password")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("password").get must contain(ErrorCodes.weakPassword))
        }
      }

      "indicates an error about a database constraint" >> {
        "when used email address" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("aui@aui.com", "first name", "last name", "passw0rd", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("email")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("email").get must contain(ErrorCodes.usedEmail))
        }
      }
    }

    "login should" >> {
      "returns a key on valid user" >> {
        val ucComp = new UserControllerForSpecificationComponent
        val userLogin = UserLogin("aui@aui.com", "abc")

        val result = ucComp.userController.login(userLogin)

        result must beAnInstanceOf[Right[_, ResultSuccessEntity[String]]]
      }

      "returns an error for invalid user" >> {
        val ucComp = new UserControllerForSpecificationComponent
        val userLogin = UserLogin("an@unknown.user", "an unknown password")

        val result = ucComp.userController.login(userLogin)

        result must beAnInstanceOf[Left[ResultError, _]]
      }

      "returns an error for valid user but invalid password" >> {
        val ucComp = new UserControllerForSpecificationComponent
        val userLogin = UserLogin("aui@aui.com", "an unknown password")

        val result = ucComp.userController.login(userLogin)

        result must beAnInstanceOf[Left[ResultError, _]]
      }
    }

    "getCurrentUserRoles should" >> {
      "returns a sequence of String" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(1, "", "", "", "", "", None, None, None, Seq())

        val result = ucComp.userController.getCurrentUserRoles

        result must beAnInstanceOf[Seq[String]]
      }

      "returns the list of roles of current user" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(1, "", "", "", "", "", None, None, None, Seq("a", "b"))

        val result = ucComp.userController.getCurrentUserRoles

        result must beEqualTo(Seq("a", "b"))
      }
    }
  }
}
