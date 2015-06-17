package vep.test.controller

import org.specs2.mutable.Specification
import spray.http.DateTime
import vep.controller.UserControllerProductionComponent
import vep.model.common._
import vep.model.user.{UserForAdmin, User, UserLogin, UserRegistration}
import vep.test.service.inmemory.VepServicesInMemoryComponent

class UserControllerForSpecificationComponent
  extends UserControllerProductionComponent
  with VepServicesInMemoryComponent {
  override def overrideServices: Boolean = false
}

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

        val result = ucComp.userController.getCurrentUserRoles.entity

        result must beAnInstanceOf[Seq[String]]
      }

      "returns the list of roles of current user" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(1, "", "", "", "", "", None, None, None, Seq("a", "b"))

        val result = ucComp.userController.getCurrentUserRoles.entity

        result must beEqualTo(Seq("a", "b"))
      }
    }

    "updatesRoles should" >> {
      "return an error when the user does not exist" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(1, "", "", "", "", "", None, None, None, Seq())

        val result = ucComp.userController.updateRoles(-1, Seq())

        result must beAnInstanceOf[Left[ResultError, _]]
      }

      "return an error when the at least one role does not exist (Seq of one role)" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(1, "", "", "", "", "", None, None, None, Seq())

        val result = ucComp.userController.updateRoles(1, Seq("unknown-role"))

        result must beAnInstanceOf[Left[ResultError, _]]
      }

      "return an error when the at least one role does not exist (Seq of 3 roles)" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(1, "", "", "", "", "", None, None, None, Seq())

        val result = ucComp.userController.updateRoles(1, Seq(Roles.user, "unknown-role", Roles.userManager))

        result must beAnInstanceOf[Left[ResultError, _]]
      }

      "updates the roles of the user" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(2, "", "", "", "", "", None, None, None, Seq())

        ucComp.userController.updateRoles(1, Seq(Roles.user, Roles.userManager))
        ucComp.userService.find(1).get.roles mustEqual Seq(Roles.user, Roles.userManager)
      }
    }

    "getUsers should" >> {
      "returns a sequence of UserForAdmin" >> {
        val ucComp = new UserControllerForSpecificationComponent
        implicit val user = User(1, "", "", "", "", "", None, None, None, Seq())

        val result = ucComp.userController.getUsers.entity

        result must beAnInstanceOf[Seq[UserForAdmin]]
      }
    }
  }
}
