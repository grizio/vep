package vep.router

import spray.http.StatusCodes
import spray.routing.{HttpService, Route}
import vep.controller.VepControllersComponent
import vep.exception.FieldErrorException
import vep.model.common.{ResultError, ResultSuccess, ResultErrors}
import vep.model.user.UserRegistration
import vep.service.VepServicesComponent

trait VepApiRouter extends HttpService with VepRouter {
  self: VepControllersComponent =>

  import spray.httpx.SprayJsonSupport.{sprayJsonMarshaller, sprayJsonUnmarshaller}
  import vep.model.common.ResultImplicits._
  import vep.model.user.UserImplicits._

  override lazy val route: Route = {
    pathPrefix("user") {
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
}
