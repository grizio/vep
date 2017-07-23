package vep.app.production.company.show.play

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.production.company.CompanyService
import vep.app.production.company.show.ShowService
import vep.app.production.theater.TheaterService
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait PlayIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext
  def companyService: CompanyService
  def showService: ShowService
  def theaterService: TheaterService

  lazy val playVerifications = new PlayVerifications(
    commonVerifications,
    theaterService
  )
  lazy val playService = new PlayService(
    configuration
  )
  lazy val playRouter = new PlayRouter(
    playVerifications,
    playService,
    companyService,
    showService,
    userService,
    executionContext
  )
}
