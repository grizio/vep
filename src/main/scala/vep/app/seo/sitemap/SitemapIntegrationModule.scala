package vep.app.seo.sitemap

import vep.Configuration
import vep.app.common.page.PageService
import vep.app.production.company.show.ShowService
import vep.app.seo.SeoCommon
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait SitemapIntegrationModule {
  def configuration: Configuration

  def userService: UserService

  def executionContext: ExecutionContext

  def showService: ShowService

  def pageServices: PageService

  def seoCommon: SeoCommon

  lazy val sitemapRouter = new SitemapRouter(
    showService,
    pageServices,
    seoCommon,
    configuration,
    userService,
    executionContext
  )
}
