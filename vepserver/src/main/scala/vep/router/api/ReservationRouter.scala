package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.actors.ReservationCreate
import vep.controller.VepControllersComponent
import vep.model.common._
import vep.model.session.ReservationFormBody
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait ReservationRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.session.ReservationImplicits._

  val reservationRoute = pathPrefix("reservation") {
    pathPrefix(Segment) { theaterCanonical =>
      pathPrefix(Segment) { sessionCanonical =>
        pathEnd {
          post {
            entity(as[ReservationFormBody]) { reservationFormBody => ctx =>
              reservationController.create(reservationFormBody.toReservationForm(theaterCanonical, sessionCanonical)) match {
                case Left(error: ResultStructuredErrors) =>
                  if (error.errors.get("theater") exists {
                    case error: ErrorItemFinal => error.contains(ErrorCodes.undefinedTheater)
                    case _ => false
                  }) {
                    ctx.complete(StatusCodes.NotFound, error)
                  } else if (error.errors.get("session") exists {
                    case error: ErrorItemFinal => error.contains(ErrorCodes.undefinedSession)
                    case _ => false
                  }) {
                    ctx.complete(StatusCodes.NotFound, error)
                  } else {
                    ctx.complete(StatusCodes.BadRequest, error)
                  }
                case Right(result) =>
                  actorRefFactory.actorSelection("/user/reservationEmailActor") ! ReservationCreate(result.entity.id)
                  ctx.complete(StatusCodes.OK, result.entity)
              }
            }
          }
        } ~ path("list") {
          get {
            sealRoute {
              authenticate(vepBasicUserAuthenticator) { implicit user =>
                authorize(user.roles.contains(Roles.sessionManager)) { ctx =>
                  reservationController.fromSession(theaterCanonical, sessionCanonical) match {
                    case Left(error) => error match {
                      case ResultError(ErrorCodes.undefinedTheater) => ctx.complete(StatusCodes.NotFound, error)
                      case ResultError(ErrorCodes.undefinedSession) => ctx.complete(StatusCodes.NotFound, error)
                      case _ => ctx.complete(StatusCodes.BadRequest, error)
                    }
                    case Right(success) => ctx.complete(StatusCodes.OK, success.entity)
                  }
                }
              }
            }
          }
        } ~ path("plan") {
          get { ctx =>
            reservationController.reservedPlacesAsPlan(theaterCanonical, sessionCanonical) match {
              case Left(error) => error match {
                case ResultError(ErrorCodes.undefinedTheater) => ctx.complete(StatusCodes.NotFound, error)
                case ResultError(ErrorCodes.undefinedSession) => ctx.complete(StatusCodes.NotFound, error)
                case _ => ctx.complete(StatusCodes.BadRequest, error)
              }
              case Right(success) => ctx.complete(StatusCodes.OK, success.entity)
            }
          }
        } ~ path("number") {
          get { ctx =>
            reservationController.reservedPlacesAsNumber(theaterCanonical, sessionCanonical) match {
              case Left(error) => error match {
                case ResultError(ErrorCodes.undefinedTheater) => ctx.complete(StatusCodes.NotFound, error)
                case ResultError(ErrorCodes.undefinedSession) => ctx.complete(StatusCodes.NotFound, error)
                case _ => ctx.complete(StatusCodes.BadRequest, error)
              }
              case Right(success) => ctx.complete(StatusCodes.OK, success.entity.toString)
            }
          }
        }
      }
    }
  }
}