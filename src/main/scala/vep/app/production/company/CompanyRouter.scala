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
    findAll ~ find ~ create ~ update
  }

  def findAll: Route = adminGet("production" / "companies") { _ =>
    Ok(companyService.findAll())
  }

  def find: Route = adminGet("production" / "companies" / Segment).apply { (companyId, _) =>
    found(companyService.find(companyId), CompanyMessage.unknownCompany) { company =>
      Ok(company)
    }
  }

  def create: Route = adminPost("production" / "companies", as[CompanyCreation]) { (companyCreation, _) =>
    verifying(companyVerifications.verifyCreation(companyCreation)) { company =>
      verifying(companyService.create(company)) { company =>
        Ok(company)
      }
    }
  }

  def update: Route = adminPut("production" / "companies" / Segment, as[Company]).apply { (companyId, company, _) =>
    verifying(companyVerifications.verify(company, companyId)) { company =>
      verifying(companyService.update(company)) { company =>
        Ok(company)
      }
    }
  }
}
