package vep.router.api

import spray.http.StatusCodes
import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.common.Roles
import vep.model.company.{CompanySeq, CompanyForm, CompanyFormBody}
import vep.model.theater.{TheaterForm, TheaterFormBody, TheaterSeq}
import vep.router.VepRouter

import scala.concurrent.ExecutionContext.Implicits.global

trait CompanyRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._
  import vep.model.company.CompanyImplicits._

  val companyRoute = pathPrefix("company") {
    path(Segment) { companyCanonical =>
      post {
        entity(as[CompanyFormBody]) { companyFormBody =>
          sealRoute {
            authenticate(vepBasicUserAuthenticator) { implicit user =>
              authorize(user.roles.contains(Roles.companyManager)) { ctx =>
                companyController.create(CompanyForm.fromCompanyFormBody(companyCanonical, companyFormBody)) match {
                  case Left(error) => ctx.complete(StatusCodes.BadRequest, error)
                  case Right(success) => ctx.complete(StatusCodes.OK, success)
                }
              }
            }
          }
        }
      } ~ put {
        entity(as[CompanyFormBody]) { companyFormBody =>
          sealRoute {
            authenticate(vepBasicUserAuthenticator) { implicit user =>
              authorize(user.roles.contains(Roles.companyManager)) { ctx =>
                companyController.update(CompanyForm.fromCompanyFormBody(companyCanonical, companyFormBody)) match {
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
  } ~ path("companies") {
    get {
      ctx => ctx.complete(StatusCodes.OK, CompanySeq(companyController.list().entity))
    }
  }
}
