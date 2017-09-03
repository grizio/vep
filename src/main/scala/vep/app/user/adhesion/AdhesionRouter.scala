package vep.app.user.adhesion

import akka.http.scaladsl.model.{ContentTypes, MediaTypes}
import akka.http.scaladsl.model.headers.{Accept, ContentDispositionTypes, `Content-Disposition`, `Content-Type`}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.HeaderDirectives.optionalHeaderValueByType
import vep.app.user.UserService
import vep.framework.router.RouterComponent
import vep.framework.utils.CsvUtils

import scala.collection.immutable
import scala.concurrent.ExecutionContext

class AdhesionRouter(
  adhesionVerifications: AdhesionVerifications,
  adhesionService: AdhesionService,
  adhesionMailer: AdhesionMailer,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAllPeriods ~ findOpenedRegistrationPeriods ~ findMine ~ findPeriod ~ findAdhesionByPeriod ~
      createPeriod ~ updatePeriod ~
      requestAdhesion ~ acceptRequestAdhesion ~ refuseRequestAdhesion
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
    val adhesions = adhesionService.findByPeriod(periodId)
    optionalHeaderValueByType[Accept]() {
      case Some(Accept(mediaTypes)) if mediaTypes.exists(_.value == MediaTypes.`text/csv`.value) =>
        Ok(
          entity = CsvUtils.write(AdhesionView.toCsv(adhesions))  ,
          headers = immutable.Seq(`Content-Type`(ContentTypes.`text/csv(UTF-8)`))
        )
      case _ =>
        Ok(adhesions)
    }
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

  def acceptRequestAdhesion: Route = flatAdminPost("user" / "adhesions" / Segment / "requests" / Segment / "accept") { (periodId, adhesionId, _) =>
    found(adhesionService.findPeriod(periodId)) { period =>
      found(adhesionService.findAdhesionByPeriod(period, adhesionId)) { adhesion =>
        verifying(adhesionService.acceptAdhesion(adhesion.id)) { _ =>
          Ok("")
        }
      }
    }
  }

  def refuseRequestAdhesion: Route = adminPost("user" / "adhesions" / Segment / "requests" / Segment / "refuse", as[String]).apply { (periodId, adhesionId, reason, _) =>
    found(adhesionService.findPeriod(periodId)) { period =>
      found(adhesionService.findAdhesionByPeriod(period, adhesionId)) { adhesion =>
        verifying(adhesionService.removeAdhesion(adhesion.id)) { _ =>
          adhesionMailer.sendRefused(adhesion, reason)
          Ok("")
        }
      }
    }
  }
}
