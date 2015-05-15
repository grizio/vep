package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.{ResultError, ResultErrors, ResultSuccess}
import vep.model.user.UserRegistration

trait UserRouter extends HttpService {
  self: VepControllersComponent =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.user.UserImplicits._
  import vep.model.common.ResultImplicits._

  val userRoute = pathPrefix("user") {
    path("register") {
      post {
        entity(as[UserRegistration]) { userRegistration => ctx =>
          userController.register(userRegistration) match {
            case ResultSuccess => ctx.complete("")
            case e: ResultErrors => ctx.complete(StatusCodes.BadRequest, e)
            case e: ResultError => ctx.complete(StatusCodes.BadRequest, e)
          }
        }
      }
    }
  }
}
