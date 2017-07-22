package vep.app.production.company

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.production.theater.{Theater, TheaterCreation, TheaterMessage}
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class CompanyRouter(
  companyVerifications: CompanyVerifications,
  companyService: CompanyService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    create
  }

  def create: Route = adminPost("production" / "companies", as[CompanyCreation]) { (companyCreation, _) =>
    verifying(companyVerifications.verifyCreation(companyCreation)) { company =>
      verifying(companyService.create(company)) { company =>
        Ok(company)
      }
    }
  }
}
