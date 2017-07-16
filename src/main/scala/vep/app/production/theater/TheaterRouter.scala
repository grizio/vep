package vep.app.production.theater

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class TheaterRouter(
  theaterVerifications: TheaterVerifications,
  theaterService: TheaterService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAll ~ create
  }

  def findAll: Route = adminGet("production" / "theaters") { _ =>
    Ok(theaterService.findAll())
  }

  def create: Route = adminPost("production" / "theaters", as[TheaterCreation]) { (theaterCreation, _) =>
    verifying(theaterVerifications.verifyCreation(theaterCreation)) { theater =>
      verifying(theaterService.create(theater)) { theater =>
        Ok(theater)
      }
    }
  }
}
