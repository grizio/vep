package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.{ErrorCodes, Roles}
import vep.model.show.{ShowFormBody, ShowSearch}
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
        } ~
        get { ctx =>
          showController.detail(showCanonical) match {
            case Left(error) => ctx.complete(if (error.code == ErrorCodes.undefinedShow) StatusCodes.NotFound else StatusCodes.BadRequest, error)
            case Right(success) => ctx.complete(StatusCodes.OK, success.entity)
          }
        }
    }
  } ~ path("shows") {
    get {
      parameters('p.as[Int] ?, 't.as[String] ?, 'a.as[String] ?, 'd.as[String] ?, 'c.as[String] ?, 'o.as[String] ?).as(ShowSearch) { showSearch => ctx =>
        showController.search(showSearch) match {
          case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
          case Right(success) => ctx.complete(StatusCodes.OK, success.entity)
        }
      }
    }
  }
}