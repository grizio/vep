package vep.app.user.registration

import java.util.UUID

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.user.{User, UserRegistration, UserRole, UserService}
import vep.framework.router.RouterComponent
import vep.framework.utils.StringUtils

import scala.concurrent.ExecutionContext

class RegistrationRouter(
  registrationService: RegistrationService,
  registrationMailer: RegistrationMailer,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    register ~ activation
  }

  def register: Route = publicPost("user" / "register", as[UserRegistration]) { userRegistration =>
    val user = User(
      id = UUID.randomUUID().toString,
      email = userRegistration.email,
      password = userRegistration.password,
      role = UserRole.user,
      authentications = Seq.empty,
      activationKey = Some(StringUtils.randomString()),
      resetPasswordKey = None
    )
    verifying(registrationService.create(user)) { user =>
      registrationMailer.send(user)
      Ok("")
    }
  }

  def activation: Route = publicGet("user" / "activation" / Segment / Segment) { (email, activationKey) =>
    verifying(registrationService.activate(email, activationKey)) { _ =>
      redirect(_
        .withPath(Uri.Path./)
        .withQuery(Uri.Query("type" -> "success", "message" -> "user.activation.done"))
      )
    }
  }
}
