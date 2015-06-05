package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.JsonImplicits
import vep.model.common.{ErrorCodes, Roles}
import vep.model.user.{RolesSeq, UserLogin, UserRegistration}
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait UserRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.user.UserImplicits._

  val userRoute = pathPrefix("user") {
    path("register") {
      post {
        entity(as[UserRegistration]) { userRegistration => ctx =>
          userController.register(userRegistration) match {
            case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
            case Right(success) => ctx.complete(StatusCodes.OK, success)
          }
        }
      }
    } ~ path("roles") {
      get {
        sealRoute {
          authenticate(vepBasicUserAuthenticator) { implicit user => ctx =>
            ctx.complete(JsonImplicits.seqToJson(userController.getCurrentUserRoles))
          }
        }
      }
    } ~ pathPrefix("roles") {
      path(LongNumber) { uid =>
        post {
          entity(as[RolesSeq]) { rolesSeq =>
            sealRoute {
              authenticate(vepBasicUserAuthenticator) { implicit user =>
                authorize(user.roles.contains(Roles.userManager)) { ctx =>
                  userController.updateRoles(uid, rolesSeq.roles) match {
                    case Left(error) => ctx.complete(if (error.code == ErrorCodes.userUnknown) StatusCodes.NotFound else StatusCodes.BadRequest, error)
                    case Right(success) => ctx.complete(StatusCodes.OK, success)
                  }
                }
              }
            }
          }
        }
      }
    } ~ path("list") {
      get {
        sealRoute {
          authenticate(vepBasicUserAuthenticator) { implicit user =>
            authorize(user.roles.contains(Roles.userManager)) { ctx =>
              ctx.complete(StatusCodes.OK, userController.getUsers)
            }
          }
        }
      }
    }
  } ~ path("login") {
    post {
      entity(as[UserLogin]) { userLogin => implicit ctx =>
        userController.login(userLogin) match {
          case Left(error) => ctx.complete(StatusCodes.Forbidden, error)
          case Right(success) => ctx.complete(StatusCodes.OK, success.entity)
        }
      }
    }
  }
}
