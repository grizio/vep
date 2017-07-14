package vep.framework.mailer

import java.util.concurrent.TimeUnit

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.routing.SmallestMailboxPool
import org.apache.commons.mail.{DefaultAuthenticator, EmailException, HtmlEmail}
import vep.Configuration

import scala.concurrent.duration.FiniteDuration

case class SmtpConfig(
  tls: Boolean = false,
  ssl: Boolean = false,
  port: Int = 25,
  host: String,
  email: String,
  user: String,
  password: String
)

case class EmailConfig(smtp: SmtpConfig, retryOn: FiniteDuration, var deliveryAttempts: Int = 0)

case class EmailMessage(
  subject: String,
  recipient: String,
  replyTo: Option[String] = None,
  text: Option[String] = None,
  html: Option[String] = None
)

case class ConfiguredEmailMessage(email: EmailMessage, config: EmailConfig)

object MailerActor {
  def emailActor(configuration: Configuration): ActorRef = ActorSystem().actorOf(
    Props(new MailerActor(configuration)).withRouter(SmallestMailboxPool(nrOfInstances = 50)),
    name = "mailer"
  )
}

/**
 * An Email sender actor that sends out email messages
 * Retries delivery up to 10 times every 5 minutes as long as it receives
 * an EmailException, gives up at any other type of exception
 */
class MailerActor(
  configuration: Configuration
) extends Actor with ActorLogging {
  private lazy val defaultSmtpConfig = SmtpConfig(
    tls = configuration.email.tls,
    ssl = configuration.email.ssl,
    port = configuration.email.port,
    host = configuration.email.host,
    email = configuration.email.email,
    user = configuration.email.user,
    password = configuration.email.password
  )

  /**
   * The actor supervisor strategy attempts to send email up to 10 times if there is a EmailException
   */
  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 10) {
      case emailException: EmailException =>
        log.debug("Restarting after receiving EmailException : {}", emailException.getMessage)
        Restart
      case unknownException: Exception =>
        log.debug("Giving up. Can you recover from this? : {}", unknownException)
        Stop
      case unknownCase: Any =>
        log.debug("Giving up on unexpected case : {}", unknownCase)
        Stop
    }

  /**
   * Forwards messages to child workers
   */
  override def receive: Receive = {
    case message: EmailMessage =>
      context.actorOf(Props[MailerWorker]) ! ConfiguredEmailMessage(
        email = message,
        config = EmailConfig(
          smtp = defaultSmtpConfig,
          retryOn = FiniteDuration(5, TimeUnit.MINUTES)
        )
      )
    case message: Any => context.actorOf(Props[MailerWorker]) ! message
  }
}

/**
 * Email worker that delivers the message
 */
class MailerWorker extends Actor with ActorLogging {
  /**
   * The email message in scope
   */
  private var emailMessage: Option[ConfiguredEmailMessage] = None

  /**
   * Delivers a message
   */
  def receive: Receive = {
    case email: ConfiguredEmailMessage =>
      emailMessage = Option(email)
      email.config.deliveryAttempts += 1
      log.debug("Attempting to deliver message")
      sendEmailSync(email)
      log.debug("Message delivered")
    case unexpectedMessage: Any =>
      log.debug("Received unexepected message : {}", unexpectedMessage)
      throw new Exception("can't handle %s".format(unexpectedMessage))
  }

  /**
   * If this child has been restarted due to an exception attempt redelivery
   * based on the message configured delay
   */
  override def preRestart(reason: Throwable, message: Option[Any]) {
    if (emailMessage.isDefined) {
      log.debug("Scheduling email message to be sent after attempts: {}", emailMessage.get)
      import context.dispatcher
      // Use this Actors' Dispatcher as ExecutionContext
      context.system.scheduler.scheduleOnce(emailMessage.get.config.retryOn, self, emailMessage.get)
    }
  }

  override def postStop() {
    if (emailMessage.isDefined) {
      log.debug("Stopped child email worker after attempts {}, {}", emailMessage.get.config.deliveryAttempts, self)
    }
  }

  /**
   * Private helper invoked by the actors that sends the email
   *
   * @param configuredEmailMessage the email message
   */
  private def sendEmailSync(configuredEmailMessage: ConfiguredEmailMessage) {
    val smtp = configuredEmailMessage.config.smtp
    val message = configuredEmailMessage.email

    val email = new HtmlEmail()
    email.setStartTLSEnabled(smtp.tls)
    email.setSSLOnConnect(smtp.ssl)
    email.setSmtpPort(smtp.port)
    email.setHostName(smtp.host)
    email.setAuthenticator(new DefaultAuthenticator(
      smtp.user,
      smtp.password
    ))

    message.text foreach email.setTextMsg

    message.html foreach email.setHtmlMsg

    email.setCharset("UTF-8")

    email.addTo(message.recipient)
      .setFrom(smtp.email)
      .setSubject(message.subject)
      .addReplyTo(message.replyTo.getOrElse(smtp.email))
      .send()
  }
}