package vep.controller

import vep.exception.FieldErrorException
import vep.model.common._
import vep.model.user.{User, UserLogin, UserRegistration}
import vep.service.VepServicesComponent

trait UserControllerComponent {
  val userController: UserController

  trait UserController {
    def register(userRegistration: UserRegistration): Either[ResultErrors, ResultSuccess]

    def login(userLogin: UserLogin): Either[ResultError, ResultSuccessEntity[String]]

    def getCurrentUserRoles(implicit currentUser: User): Seq[String]

    def updateRoles(uid: Long, roles: Seq[String])(implicit currentUser: User): Either[ResultError, ResultSuccess]
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

    override def getCurrentUserRoles(implicit user: User): Seq[String] = user.roles

    override def updateRoles(uid: Long, roles: Seq[String])(implicit currentUser: User): Either[ResultError, ResultSuccess] = {
      if (roles exists { role => !Roles.acceptedRoles.contains(role) }) {
        Left(ResultError(ErrorCodes.roleUnknown))
      } else {
        userService.find(uid) match {
          case Some(user) =>
            userService.updateRoles(user.copy(roles = roles))
            Right(ResultSuccess)
          case None =>
            Left(ResultError(ErrorCodes.userUnknown))
        }
      }
    }
  }

}
