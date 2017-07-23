package vep.app.production.company.show.play

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.production.company.CompanyService
import vep.app.production.company.show.ShowService
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class PlayRouter(
  playVerifications: PlayVerifications,
  playService: PlayService,
  companyService: CompanyService,
  showService: ShowService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    create
  }

  def create: Route = adminPost("production" / "companies" / Segment / "shows" / Segment / "plays", as[PlayCreation]).apply { (companyId, showId, playCreation, _) =>
    found(companyService.find(companyId)) { company =>
      found(showService.findFromCompany(company, showId)) { show =>
        verifying(playVerifications.verifyCreation(playCreation)) { play =>
          verifying(playService.create(play, show)) { play =>
            Ok(play)
          }
        }
      }
    }
  }
}
