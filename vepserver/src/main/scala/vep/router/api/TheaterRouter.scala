package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.Roles
import vep.model.theater.{TheaterForm, TheaterFormBody}
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait TheaterRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.theater.TheaterImplicits._

  val theaterRoute = pathPrefix("theater") {
    path(Segment) { theaterCanonical =>
      post {
        entity(as[TheaterFormBody]) { theaterFormBody =>
          sealRoute {
            authenticate(vepBasicUserAuthenticator) { implicit user =>
              authorize(user.roles.contains(Roles.pageManager)) { ctx =>
                theaterController.create(TheaterForm.fromTheaterFormBody(theaterCanonical, theaterFormBody)) match {
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
