package vep.app.production.theater

import vep.Configuration
import vep.app.common.CommonVerifications
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait TheaterIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val theaterVerifications = new TheaterVerifications(
    commonVerifications
  )
  lazy val theaterService = new TheaterService(
    configuration
  )
  lazy val theaterRouter = new TheaterRouter(
    theaterVerifications,
    theaterService,
    userService,
    executionContext
  )
}
