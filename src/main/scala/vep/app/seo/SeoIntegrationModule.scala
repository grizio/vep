package vep.app.seo

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.seo.sitemap.SitemapIntegrationModule
import vep.app.seo.views.ViewsIntegrationModule
import vep.app.user.UserService

trait SeoIntegrationModule
  extends SitemapIntegrationModule
    with ViewsIntegrationModule {

  def configuration: Configuration

  def userService: UserService

  lazy val seoRoute: Route = sitemapRouter.route ~ viewRouter.route
}
