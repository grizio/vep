package vep.app.production.reservation

import akka.actor.ActorSystem
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
  def system: ActorSystem
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
  lazy val reservationAnonymizer = new ReservationAnonymizer(playService, reservationService, system, executionContext)
}
