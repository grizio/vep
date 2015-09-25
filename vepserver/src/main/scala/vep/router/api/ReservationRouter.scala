package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common._
import vep.model.session.{ReservationFormBody, SessionFormBody, SessionSearch, SessionUpdateFormBody}
import vep.router.VepRouter

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
                case Right(result) => ctx.complete(StatusCodes.OK, result.entity)
              }
            }
          }
        }
      }
    }
  }
}