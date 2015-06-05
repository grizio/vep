package vep.controller

import vep.exception.FieldErrorException
import vep.model.common._
import vep.model.user.{User, UserForAdmin, UserLogin, UserRegistration}
import vep.service.VepServicesComponent

/**
 * This controller defines actions querying or updating application users.
 */
trait UserControllerComponent {
  val userController: UserController

  trait UserController {
    /**
     * Inserts a user into database
     * @param userRegistration The user to insert
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success
     */
    def register(userRegistration: UserRegistration): Either[ResultErrors, ResultSuccess]

    /**
     * Logs in the user and creates a new key for session
     * @param userLogin The user identification data to use to check its identification
     * @return An error if the user does not exist or its session key
     */
    def login(userLogin: UserLogin): Either[ResultError, ResultSuccessEntity[String]]

    /**
     * Returns the list of roles of the given user.
     * @param currentUser The connected user
     * @return The list of roles of the given user
     */
    def getCurrentUserRoles(implicit currentUser: User): ResultSuccessEntity[Seq[String]]

    /**
     * Updates the list of roles of the given user.
     * @param uid The id of the user to update
     * @param roles The new list of roles
     * @param currentUser The connected user
     * @return An error if a role does not exist or the user is undefined otherwise a success
     */
    def updateRoles(uid: Long, roles: Seq[String])(implicit currentUser: User): Either[ResultError, ResultSuccess]

    /**
     * Returns the whole list of users from the database
     * @param currentUser The connected user
     * @return The list of users
     */
    def getUsers(implicit currentUser: User): ResultSuccessEntity[Seq[UserForAdmin]]
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

    override def getCurrentUserRoles(implicit user: User): ResultSuccessEntity[Seq[String]] = ResultSuccessEntity(user.roles)

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

    override def getUsers(implicit currentUser: User): ResultSuccessEntity[Seq[UserForAdmin]] = ResultSuccessEntity(userService.findAllForAdmin())
  }

}
