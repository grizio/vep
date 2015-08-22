package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.Roles
import vep.model.session.SessionFormBody
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait SessionRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.session.SessionImplicits._

  val sessionRoute = pathPrefix("session") {
    path(Segment) { theaterCanonical =>
      post {
        entity(as[SessionFormBody]) { sessionFormBody =>
          sealRoute {
            authenticate(vepBasicUserAuthenticator) { implicit user =>
              authorize(user.roles.contains(Roles.sessionManager)) { ctx =>
                sessionController.create(sessionFormBody.toSessionForm(theaterCanonical)) match {
                  case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
                  case Right(success) => ctx.complete(StatusCodes.OK, success.entity)
                }
              }
            }
          }
        }
      }
    }
  }
}