package vep.app.common.contact

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class ContactRouter(
  contactVerifications: ContactVerifications,
  contactMailer: ContactMailer,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    contact
  }

  def contact: Route = publicPost("contact", as[Contact]) { contact =>
    verifying(contactVerifications.verify(contact)) { validContact =>
      contactMailer.send(validContact)
      Ok("")
    }
  }
}
