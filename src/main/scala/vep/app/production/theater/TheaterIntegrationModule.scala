package vep.app.production.theater

import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait TheaterIntegrationModule {
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val theaterVerifications = new TheaterVerifications(
    commonVerifications
  )
  lazy val theaterService = new TheaterService()
  lazy val theaterRouter = new TheaterRouter(
    theaterVerifications,
    theaterService,
    userService,
    executionContext
  )
}
