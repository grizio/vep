package vep.app.production.company

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class CompanyRouter(
  companyVerifications: CompanyVerifications,
  companyService: CompanyService,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAll ~ find ~ create ~ update ~ delete
  }

  def findAll: Route = adminGet("production" / "companies") { _ =>
    Ok(companyService.findAll())
  }

  def find: Route = publicGet("production" / "companies" / Segment) { companyId =>
    found(companyService.find(companyId), CompanyMessage.unknownCompany) { company =>
      Ok(company)
    }
  }

  def create: Route = adminPost("production" / "companies", as[CompanyCreation]) { (companyCreation, _) =>
    val company = Company(
      id = UUID.randomUUID().toString,
      name = companyCreation.name,
      address = companyCreation.address,
      isVep = companyCreation.isVep,
      content = companyCreation.content
    )
    verifying(companyService.create(company)) { company =>
      Ok(company)
    }
  }

  def update: Route = adminPut("production" / "companies" / Segment, as[Company]).apply { (companyId, company, _) =>
    verifying(companyVerifications.verify(company, companyId)) { company =>
      verifying(companyService.update(company)) { company =>
        Ok(company)
      }
    }
  }

  def delete: Route = adminDelete("production" / "companies" / Segment).apply { (companyId, _) =>
    verifying(companyService.delete(companyId)) { _ =>
      Ok("")
    }
  }
}
