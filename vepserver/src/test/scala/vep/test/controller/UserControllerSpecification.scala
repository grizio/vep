package vep.test.controller

import org.specs2.mutable.Specification
import vep.controller.UserControllerProductionComponent
import vep.model.common.{ErrorCodes, ResultErrors, ResultSuccess}
import vep.model.user.UserRegistration
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

        result === ResultSuccess
      }

      "refuse an entity with invalid fields" >> {
        val ucComp = new UserControllerForSpecificationComponent
        val userRegistration = UserRegistration("", "", "", "", None)

        val result = ucComp.userController.register(userRegistration)

        result must beAnInstanceOf[ResultErrors]
      }

      "indicates an error about a field value" >> {
        "when empty email" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[ResultErrors]) and
            (result.asInstanceOf[ResultErrors].errors must haveKey("email")) and
            (result.asInstanceOf[ResultErrors].errors.get("email").get must contain(ErrorCodes.emptyEmail))
        }

        "when invalid email" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("abc.abc", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[ResultErrors]) and
            (result.asInstanceOf[ResultErrors].errors must haveKey("email")) and
            (result.asInstanceOf[ResultErrors].errors.get("email").get must contain(ErrorCodes.invalidEmail))
        }

        "when empty first name" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[ResultErrors]) and
            (result.asInstanceOf[ResultErrors].errors must haveKey("firstName")) and
            (result.asInstanceOf[ResultErrors].errors.get("firstName").get must contain(ErrorCodes.emptyFirstName))
        }

        "when empty last name" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[ResultErrors]) and
            (result.asInstanceOf[ResultErrors].errors must haveKey("lastName")) and
            (result.asInstanceOf[ResultErrors].errors.get("lastName").get must contain(ErrorCodes.emptyLastName))
        }

        "when empty password" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[ResultErrors]) and
            (result.asInstanceOf[ResultErrors].errors must haveKey("password")) and
            (result.asInstanceOf[ResultErrors].errors.get("password").get must contain(ErrorCodes.emptyPassword))
        }

        "when not secured password" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("", "", "", "password", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[ResultErrors]) and
            (result.asInstanceOf[ResultErrors].errors must haveKey("password")) and
            (result.asInstanceOf[ResultErrors].errors.get("password").get must contain(ErrorCodes.weakPassword))
        }
      }

      "indicates an error about a database constraint" >> {
        "when used email address" >> {
          val ucComp = new UserControllerForSpecificationComponent
          val userRegistration = UserRegistration("aui@aui.com", "first name", "last name", "passw0rd", None)

          val result = ucComp.userController.register(userRegistration)

          (result must beAnInstanceOf[ResultErrors]) and
            (result.asInstanceOf[ResultErrors].errors must haveKey("email")) and
            (result.asInstanceOf[ResultErrors].errors.get("email").get must contain(ErrorCodes.usedEmail))
        }
      }
    }
  }
}
