package vep.router.api

import spray.routing.HttpService
import vep.controller.VepControllersComponent
import vep.model.user.UserRegistration
import vep.router.VepRouter

trait UserRouter extends HttpService {
  self: VepControllersComponent with VepRouter =>

  import spray.httpx.SprayJsonSupport._
  import vep.model.user.UserImplicits._

  val userRoute = pathPrefix("user") {
    path("register") {
      post {
        entity(as[UserRegistration]) { userRegistration => implicit ctx =>
          processResult(userController.register(userRegistration))
        }
      }
    }
  }
}
