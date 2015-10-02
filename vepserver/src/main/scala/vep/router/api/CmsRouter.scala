package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.cms._
import vep.model.common.Roles
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This trait defines routes and rules for each possible action impacting cms.
 */
trait CmsRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.cms.ContactImplicit._
  import vep.model.cms.PageImplicits._
  import vep.model.common.ResultImplicits._

  val cmsRoute = pathPrefix("cms") {
    pathPrefix("page") {
      path(Segment) { pageCanonical =>
        post {
          entity(as[PageFormBody]) { pageFormBody =>
            sealRoute {
              authenticate(vepBasicUserAuthenticator) { implicit user =>
                authorize(user.roles.contains(Roles.theaterManager)) { ctx =>
                  pageController.create(PageForm.fromPageFormBody(pageCanonical, pageFormBody)) match {
                    case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
                    case Right(success) => ctx.complete(StatusCodes.OK, success)
                  }
                }
              }
            }
          }
        } ~ put {
          entity(as[PageFormBody]) { pageFormBody =>
            sealRoute {
              authenticate(vepBasicUserAuthenticator) { implicit user =>
                authorize(user.roles.contains(Roles.theaterManager)) { ctx =>
                  pageController.update(PageForm.fromPageFormBody(pageCanonical, pageFormBody)) match {
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
    } ~ path("pages") {
      get { ctx =>
        ctx.complete(StatusCodes.OK, PageSeq(pageController.list().entity))
      }
    }
  } ~ path("contact") {
    post {
      entity(as[Contact]) { contact => ctx =>
        contactController.post(contact) match {
          case Left(errors) => ctx.complete(StatusCodes.BadRequest, errors)
          case Right(_) => ctx.complete(StatusCodes.OK)
        }
      }
    }
  }
}
