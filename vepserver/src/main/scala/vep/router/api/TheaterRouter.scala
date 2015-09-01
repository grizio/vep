package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.Roles
import vep.model.theater.{TheaterForm, TheaterFormBody, TheaterSeq}
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait TheaterRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.theater.TheaterImplicits._

  val theaterRoute = pathPrefix("theater") {
    pathPrefix(Segment) { theaterCanonical =>
      path("locked") {
        get { ctx =>
          theaterController.isLocked(theaterCanonical) match {
            case Left(error) => ctx.complete(StatusCodes.NotFound, error.code.toString)
            case Right(success) => ctx.complete(StatusCodes.OK, success.entity.toString)
          }
        }
      } ~ pathEnd {
        post {
          entity(as[TheaterFormBody]) { theaterFormBody =>
            sealRoute {
              authenticate(vepBasicUserAuthenticator) { implicit user =>
                authorize(user.roles.contains(Roles.theaterManager)) { ctx =>
                  theaterController.create(TheaterForm.fromTheaterFormBody(theaterCanonical, theaterFormBody)) match {
                    case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
                    case Right(success) => ctx.complete(StatusCodes.OK, success)
                  }
                }
              }
            }
          }
        } ~ put {
          entity(as[TheaterFormBody]) { theaterFormBody =>
            sealRoute {
              authenticate(vepBasicUserAuthenticator) { implicit user =>
                authorize(user.roles.contains(Roles.theaterManager)) { ctx =>
                  theaterController.update(TheaterForm.fromTheaterFormBody(theaterCanonical, theaterFormBody)) match {
                    case Left(error) =>
                      if (error.errors.contains("canonical")) {
                        ctx.complete(StatusCodes.NotFound, error)
                      } else {
                        ctx.complete(StatusCodes.BadRequest, error)
                      }
                    case Right(success) => ctx.complete(StatusCodes.OK, success)
                  }
                }
              }
            }
          }
        }
      }
    }
  } ~ path("theaters") {
    get {
      ctx => ctx.complete(StatusCodes.OK, TheaterSeq(theaterController.list().entity))
    }
  }
}
