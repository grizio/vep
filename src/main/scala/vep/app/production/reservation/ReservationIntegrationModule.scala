package vep.app.production.reservation

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.production.company.show.play.PlayService
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait ReservationIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext
  def playService: PlayService

  lazy val reservationVerifications = new ReservationVerifications(
    commonVerifications,
    reservationService
  )
  lazy val reservationService = new ReservationService(
    configuration
  )
  lazy val reservationRouter = new ReservationRouter(
    reservationVerifications,
    reservationService,
    playService,
    userService,
    executionContext
  )
}
