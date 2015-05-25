package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.user.{UserLogin, UserRegistration}
import vep.router.VepRouter

trait UserRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.user.UserImplicits._
  import vep.model.common.ResultImplicits._

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
