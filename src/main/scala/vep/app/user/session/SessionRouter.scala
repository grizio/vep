package vep.app.user.session

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.user.UserService
import vep.framework.router.RouterComponent
import vep.framework.utils.StringUtils

import scala.concurrent.ExecutionContext

class SessionRouter(
  sessionService: SessionService,
  sessionMailer: SessionMailer,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  val route: Route = {
    login ~ session ~ requestResetPassword ~ resetPassword
  }

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

  def requestResetPassword: Route = {
    publicPost("user" / "password" / "requestReset", as[String]) { jsEmail =>
      val email = StringUtils.withoutQuote(jsEmail)
      verifying(sessionService.createResetPasswordKey(email)) { resetPasswordKey =>
        sessionMailer.sendResetToken(email, resetPasswordKey)
        Ok("")
      }
    }
  }

  def resetPassword: Route = {
    publicPost("user" / "password" / "reset", as[ResetPassword]) { resetPassword =>
      verifying(sessionService.resetPassword(resetPassword)) { _ =>
        Ok("")
      }
    }
  }
}
