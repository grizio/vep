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
    findAll ~ find ~ create ~ update
  }

  def findAll: Route = adminGet("production" / "theaters") { _ =>
    Ok(theaterService.findAll())
  }

  def find: Route = adminGet("production" / "theaters" / Segment).apply { (theaterId, _) =>
    found(theaterService.find(theaterId), TheaterMessage.unknownTheater) { theater =>
      Ok(theater)
    }
  }

  def create: Route = adminPost("production" / "theaters", as[TheaterCreation]) { (theaterCreation, _) =>
    verifying(theaterVerifications.verifyCreation(theaterCreation)) { theater =>
      verifying(theaterService.create(theater)) { theater =>
        Ok(theater)
      }
    }
  }

  def update: Route = adminPut("production" / "theaters" / Segment, as[Theater]).apply { (theaterId, theater, _) =>
    verifying(theaterVerifications.verify(theater, theaterId)) { theater =>
      verifying(theaterService.update(theater)) { theater =>
        Ok(theater)
      }
    }
  }
}
