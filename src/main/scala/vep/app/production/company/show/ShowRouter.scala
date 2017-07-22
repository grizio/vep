package vep.app.production.company.show

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.production.company.CompanyService
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class ShowRouter(
  showVerifications: ShowVerifications,
  showService: ShowService,
  companyService: CompanyService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAll ~ create
  }

  def findAll: Route = adminGet("production" / "companies" / Segment / "shows").apply { (companyId, _) =>
    found(companyService.find(companyId)) { company =>
      Ok(showService.findByCompany(company))
    }
  }

  def create: Route = adminPost("production" / "companies" / Segment / "shows", as[ShowCreation]).apply { (companyId, showCreation, _) =>
    found(companyService.find(companyId)) { company =>
      verifying(showVerifications.verifyCreation(showCreation)) { show =>
        verifying(showService.create(show, company)) { show =>
          Ok(show)
        }
      }
    }
  }
}
