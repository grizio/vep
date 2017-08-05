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
    findAllPeriods ~ findOpenedRegistrationPeriods ~ findMine ~ findPeriod ~ findAdhesionByPeriod ~
      createPeriod ~ updatePeriod ~
      requestAdhesion
  }

  def findAllPeriods: Route = adminGet("user" / "adhesions") { _ =>
    Ok(adhesionService.findAllPeriods())
  }

  def findOpenedRegistrationPeriods: Route = adminGet("user" / "adhesions" / "opened").apply { (_) =>
    Ok(adhesionService.findOpenedPeriods())
  }

  def findMine: Route = userGet("user" / "adhesions" / "mine") { user =>
    Ok(adhesionService.findByUser(user))
  }

  def findPeriod: Route = adminGet("user" / "adhesions" / Segment).apply { (periodId, _) =>
    found(adhesionService.findPeriod(periodId)) { period =>
      Ok(period)
    }
  }

  def findAdhesionByPeriod: Route = adminGet("user" / "adhesions" / Segment / "adhesions").apply { (periodId, _) =>
    Ok(adhesionService.findByPeriod(periodId))
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

  def requestAdhesion: Route = userPost("user" / "adhesions" / Segment / "requests", as[RequestAdhesion]).apply { (periodId, adhesion, user) =>
    found(adhesionService.findPeriod(periodId)) { period =>
      verifying(adhesionVerifications.verifyRequestAdhesion(adhesion, period, user)) { validRequestAdhesion =>
        verifying(adhesionService.createAdhesion(validRequestAdhesion, period)) { savedAdhesion =>
          Ok(savedAdhesion)
        }
      }
    }
  }
}
