package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.Roles
import vep.model.session.{SessionFormBody, SessionSearch, SessionUpdateFormBody}
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait SessionRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.session.SessionImplicits._

  val sessionRoute = pathPrefix("session") {
    pathPrefix(Segment) { theaterCanonical =>
      pathEnd {
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
      } ~
        path(Segment) { sessionCanonical =>
          get {
            sealRoute {
              optionalAuthenticate(vepBasicUserAuthenticator) { implicit user => ctx =>
                sessionController.find(theaterCanonical, sessionCanonical).entity match {
                  case Some(session) =>
                    if (session.date.isAfterNow) {
                      ctx.complete(StatusCodes.OK, session)
                    } else if (user.isDefined) {
                      if (user.get.roles.contains(Roles.sessionManager)) {
                        ctx.complete(StatusCodes.OK, session)
                      } else {
                        ctx.complete(StatusCodes.Forbidden)
                      }
                    } else {
                      ctx.complete(StatusCodes.Unauthorized)
                    }
                  case None => ctx.complete(StatusCodes.NotFound)
                }
              }
            }
          } ~
            put {
              entity(as[SessionUpdateFormBody]) { sessionUpdateFormBody =>
                sealRoute {
                  authenticate(vepBasicUserAuthenticator) { implicit user =>
                    authorize(user.roles.contains(Roles.sessionManager)) { ctx =>
                      sessionController.update(sessionUpdateFormBody.toSessionUpdateForm(theaterCanonical, sessionCanonical)) match {
                        case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
                        case Right(success) => ctx.complete(StatusCodes.OK)
                      }
                    }
                  }
                }
              }
            }
        }
    }
  } ~ path("sessions") {
    get {
      parameters('t.as[String] ?, 's.as[String] ?, 'sd.as[String] ?, 'ed.as[String] ?, 'o.as[String] ?, 'p.as[Int] ?).as(SessionSearch) { sessionSearch => ctx =>
        sessionController.search(sessionSearch) match {
          case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
          case Right(success) => ctx.complete(StatusCodes.OK, success.entity)
        }
      }
    }
  }
}