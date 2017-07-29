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
    findAll ~ find ~ findNext ~ findNextFull ~ create ~ update ~ delete
  }

  def findAll: Route = adminGet("production" / "companies" / Segment / "shows" / Segment / "plays").apply { (companyId, showId, _) =>
    found(companyService.find(companyId)) { company =>
      found(showService.findFromCompany(company, showId)) { show =>
        Ok(playService.findByShow(show))
      }
    }
  }

  def find: Route = adminGet("production" / "companies" / Segment / "shows" / Segment / "plays" / Segment).apply { (companyId, showId, playId, _) =>
    found(companyService.find(companyId)) { company =>
      found(showService.findFromCompany(company, showId)) { show =>
        found(playService.findFromShow(show, playId)) { play =>
          Ok(play)
        }
      }
    }
  }

  def findNext: Route = publicGet("production" / "plays" / "next") {
    Ok(playService.findNext())
  }

  def findNextFull: Route = publicGet("production" / "plays" / "nextFull") {
    Ok(playService.findNextFull())
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

  def update: Route = adminPut("production" / "companies" / Segment / "shows" / Segment / "plays" / Segment, as[Play]).apply { (companyId, showId, playId, play, _) =>
    found(companyService.find(companyId)) { company =>
      found(showService.findFromCompany(company, showId)) { _ =>
        verifying(playVerifications.verify(play, playId)) { play =>
          verifying(playService.update(play)) { play =>
            Ok(play)
          }
        }
      }
    }
  }

  def delete: Route = adminDelete("production" / "companies" / Segment / "shows" / Segment / "plays" / Segment).apply { (companyId, showId, playId, _) =>
    verifying(playService.delete(companyId, showId, playId)) { _ =>
      Ok("")
    }
  }
}