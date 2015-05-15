package vep.controller

import spray.http.StatusCodes
import spray.routing.RequestContext
import vep.exception.FieldErrorException
import vep.model.common.{ResultSuccess, Result}
import vep.model.user.UserRegistration
import vep.service.VepServicesComponent

trait UserControllerComponent {
  val userController: UserController

  trait UserController {
    def register(userRegistration: UserRegistration): Result
  }

}

trait UserControllerProductionComponent extends UserControllerComponent {
  self: VepServicesComponent =>

  override val userController: UserController = new UserControllerProduction

  class UserControllerProduction extends UserController {
    override def register(userRegistration: UserRegistration): Result = {
      if (userRegistration.verify) {
        try {
          userService.register(userRegistration)
          ResultSuccess
        } catch {
          case e: FieldErrorException => e.toResultErrors
        }
      } else {
        userRegistration.toResult
      }
    }
  }

}
