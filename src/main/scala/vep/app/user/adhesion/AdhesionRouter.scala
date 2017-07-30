package vep.app.user.adhesion

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class AdhesionRouter(
  adhesionVerifications: AdhesionVerifications,
  adhesionService: AdhesionService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAllPeriods ~ createPeriod
  }

  def findAllPeriods: Route = adminGet("user" / "adhesions") { _ =>
    Ok(adhesionService.findAllPeriods())
  }

  def createPeriod: Route = adminPost("user" / "adhesions", as[PeriodAdhesionCreation]) { (periodAdhesion, _) =>
    verifying(adhesionVerifications.verifyPeriod(periodAdhesion)) { validPeriodAdhesion =>
      verifying(adhesionService.createPeriod(validPeriodAdhesion)) { savedPeriodAdhesion =>
        Ok(savedPeriodAdhesion)
      }
    }
  }
}
