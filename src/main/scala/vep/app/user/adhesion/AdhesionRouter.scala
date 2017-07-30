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
    findAllPeriods ~ findPeriod ~ createPeriod ~ updatePeriod
  }

  def findAllPeriods: Route = adminGet("user" / "adhesions") { _ =>
    Ok(adhesionService.findAllPeriods())
  }

  def findPeriod: Route = adminGet("user" / "adhesions" / Segment).apply { (periodId, _) =>
    Ok(adhesionService.findPeriod(periodId))
  }

  def createPeriod: Route = adminPost("user" / "adhesions", as[PeriodAdhesionCreation]) { (periodAdhesion, _) =>
    verifying(adhesionVerifications.verifyPeriod(periodAdhesion)) { validPeriodAdhesion =>
      verifying(adhesionService.createPeriod(validPeriodAdhesion)) { savedPeriodAdhesion =>
        Ok(savedPeriodAdhesion)
      }
    }
  }

  def updatePeriod: Route = adminPut("user" / "adhesions" / Segment, as[PeriodAdhesion]).apply { (periodId, periodAdhesion, _) =>
    found(adhesionService.findPeriod(periodId)) { _ =>
      verifying(adhesionVerifications.verifyPeriodUpdate(periodAdhesion, periodId)) { validPeriodAdhesion =>
        verifying(adhesionService.updatePeriod(validPeriodAdhesion)) { savedPeriod =>
          Ok(savedPeriod)
        }
      }
    }
  }
}
