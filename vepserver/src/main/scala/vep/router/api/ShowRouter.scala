package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.Roles
import vep.model.show.ShowFormBody
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait ShowRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.show.ShowImplicits._

  val showRoute = pathPrefix("show") {
    path(Segment) { showCanonical =>
      post {
        entity(as[ShowFormBody]) { showFormBody =>
          sealRoute {
            authenticate(vepBasicUserAuthenticator) { implicit user =>
              authorize(user.roles.contains(Roles.showManager)) { ctx =>
                showController.create(showFormBody.toShowForm(showCanonical)) match {
                  case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
                  case Right(success) => ctx.complete(StatusCodes.OK, success)
                }
              }
            }
          }
        }
      } ~
        put {
          entity(as[ShowFormBody]) { showFormBody =>
            sealRoute {
              authenticate(vepBasicUserAuthenticator) { implicit user =>
                authorize(user.roles.contains(Roles.showManager)) { ctx =>
                  showController.update(showFormBody.toShowForm(showCanonical)) match {
                    case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
                    case Right(success) => ctx.complete(StatusCodes.OK, success)
                  }
                }
              }
            }
          }
        }
    }
  }
}