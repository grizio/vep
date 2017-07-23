package vep.app.common.contact

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService
import vep.framework.mailer.Mailer

import scala.concurrent.ExecutionContext

trait ContactIntegrationModule {
  def mailer: Mailer

  def configuration: Configuration

  def userService: UserService

  def commonVerifications: CommonVerifications

  def executionContext: ExecutionContext

  lazy val contactMailer = new ContactMailer(
    mailer,
    configuration
  )
  lazy val contactVerifications = new ContactVerifications(
    commonVerifications
  )
  lazy val contactRouter = new ContactRouter(
    contactVerifications,
    contactMailer,
    userService,
    executionContext
  )
}
