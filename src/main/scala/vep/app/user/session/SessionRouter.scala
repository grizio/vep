package vep.app.user.session

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class SessionRouter(
  sessionService: SessionService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  val route: Route = login ~ session

  def login: Route = publicPost("user" / "login", as[UserLogin]) { userLogin =>
    verifying(sessionService.login(userLogin)) { authentication =>
      Ok(authentication)
    }
  }

  def session: Route = {
    userGet("user" / "session") { user =>
      Ok(UserSession(
        email = user.email,
        role = user.role
      ))
    }
  }
}
