package vep.app.production.reservation

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.production.company.show.play.PlayService
import vep.app.user.UserService
import vep.framework.mailer.Mailer

import scala.concurrent.ExecutionContext

trait ReservationIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext
  def playService: PlayService
  def mailer: Mailer

  lazy val reservationVerifications = new ReservationVerifications(
    commonVerifications,
    reservationService
  )
  lazy val reservationService = new ReservationService()
  lazy val reservationMailer = new ReservationMailer(
    mailer,
    configuration
  )
  lazy val reservationRouter = new ReservationRouter(
    reservationVerifications,
    reservationService,
    reservationMailer,
    playService,
    configuration,
    userService,
    executionContext
  )
}
