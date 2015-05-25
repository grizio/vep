package vep.controller

import vep.exception.FieldErrorException
import vep.model.common._
import vep.model.user.{UserLogin, UserRegistration}
import vep.service.VepServicesComponent

trait UserControllerComponent {
  val userController: UserController

  trait UserController {
    def register(userRegistration: UserRegistration): Either[ResultErrors, ResultSuccess]

    def login(userLogin: UserLogin): Either[ResultError, ResultSuccessEntity[String]]
  }

}

trait UserControllerProductionComponent extends UserControllerComponent {
  self: VepServicesComponent =>

  override val userController: UserController = new UserControllerProduction

  class UserControllerProduction extends UserController {
    override def register(userRegistration: UserRegistration): Either[ResultErrors, ResultSuccess] = {
      if (userRegistration.verify) {
        try {
          userService.register(userRegistration)
          Right(ResultSuccess)
        } catch {
          case e: FieldErrorException => Left(e.toResultErrors)
        }
      } else {
        Left(userRegistration.toResult.asInstanceOf[ResultErrors])
      }
    }

    override def login(userLogin: UserLogin): Either[ResultError, ResultSuccessEntity[String]] = {
      if (userLogin.verify) {
        userService.login(userLogin) match {
          case Some(user) => Right(ResultSuccessEntity(user.keyLogin.get))
          case None => Left(ResultError(ErrorCodes.unknownUser))
        }
      } else {
        Left(userLogin.toResult.asInstanceOf[ResultError])
      }
    }
  }

}
