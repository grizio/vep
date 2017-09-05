package vep.app.production.company.show

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.production.company.CompanyService
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class ShowRouter(
  showVerifications: ShowVerifications,
  showService: ShowService,
  companyService: CompanyService,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAll ~ find ~ findNext ~ findAllWithDependencies ~ create ~ update ~ delete
  }

  def findAll: Route = publicGet("production" / "companies" / Segment / "shows") { companyId =>
    found(companyService.find(companyId)) { company =>
      Ok(showService.findByCompany(company))
    }
  }

  def find: Route = publicGet("production" / "companies" / Segment / "shows" / Segment) { (companyId, showId) =>
    found(companyService.find(companyId)) { company =>
      found(showService.findFromCompany(company, showId)) { show =>
        Ok(show)
      }
    }
  }

  def findNext: Route = publicGet("production" / "shows" / "next") {
    Ok(showService.findNext())
  }

  def findAllWithDependencies: Route = publicGet("production" / "shows" / "full") {
    Ok(showService.findAllWithDependencies())
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

  def update: Route = adminPut("production" / "companies" / Segment / "shows" / Segment, as[Show]).apply { (companyId, showId, show, _) =>
    found(companyService.find(companyId)) { company =>
      verifying(showVerifications.verify(show, showId)) { show =>
        verifying(showService.update(show)) { show =>
          Ok(show)
        }
      }
    }
  }

  def delete: Route = adminDelete("production" / "companies" / Segment / "shows" / Segment).apply { (companyId, showId, _) =>
    verifying(showService.delete(companyId, showId)) { show =>
      Ok("")
    }
  }
}
