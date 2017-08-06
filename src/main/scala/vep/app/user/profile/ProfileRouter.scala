package vep.app.user.profile

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.user.{Profile, UserService}
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class ProfileRouter(
  profileVerifications: ProfileVerifications,
  profileService: ProfileService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    currentProfile ~ update
  }

  def currentProfile: Route = userGet("user" / "profile") { user =>
    Ok(profileService.findByUser(user))
  }

  def update: Route = userPut("user" / "profile", as[Profile]) { (profile, user) =>
    verifying(profileVerifications.verify(profile, user)) { _ =>
      verifying(profileService.update(profile, user)) { savedProfile =>
        Ok(savedProfile)
      }
    }
  }
}