package vep.app.seo.views

import vep.Configuration
import vep.app.common.page.PageService
import vep.app.production.company.CompanyService
import vep.app.production.company.show.ShowService
import vep.app.production.company.show.play.PlayService
import vep.app.seo.SeoCommon
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait ViewsIntegrationModule {
  def configuration: Configuration

  def userService: UserService

  def executionContext: ExecutionContext

  def companyService: CompanyService

  def showService: ShowService

  def playService: PlayService

  def pageServices: PageService

  def seoCommon: SeoCommon

  lazy val viewRouter = new ViewsRouter(
    companyService,
    showService,
    playService,
    pageServices,
    seoCommon,
    configuration,
    userService,
    executionContext
  )
}
