package vep.framework.mailer

import vep.Configuration

trait Mailer {
  def send(email: String, title: String, content: String, replyToVep: Boolean = false): Unit
}

class DefaultMailer(
  configuration: Configuration
) extends Mailer {
  private val mailerActor = MailerActor.emailActor(configuration)

  override def send(email: String, title: String, content: String, replyToVep: Boolean = false): Unit = {
    mailerActor ! EmailMessage(
      subject = title,
      recipient = email,
      html = Some(content),
      text = Some(content),
      replyTo = if (replyToVep) Some(configuration.email.replyTo) else None
    )
  }
}

class ConsoleMailer extends Mailer {
  override def send(email: String, title: String, content: String, replyToVep: Boolean = false): Unit = {
    println(
      s"""
         |=====
         |Mail start
         |To: $email
         |Title: $title
         |Content: $content
         |Mail end
         |=====
      """.stripMargin)
  }
}
