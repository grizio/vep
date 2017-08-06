package vep.app.common

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import vep.Configuration
import vep.app.common.blog.BlogIntegrationModule
import vep.app.common.contact.ContactIntegrationModule
import vep.app.common.page.PageIntegrationModule
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService

trait CommonIntegrationModule
  extends ContactIntegrationModule
    with PageIntegrationModule
    with BlogIntegrationModule {

  def configuration: Configuration
  def userService: UserService

  lazy val commonVerifications = new CommonVerifications()

  lazy val commonRoute: Route = contactRouter.route ~ pageRouter.route ~ blogRouter.route
}
