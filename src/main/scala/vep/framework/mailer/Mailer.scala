package vep.framework.mailer

import vep.Configuration

trait Mailer {
  def send(email: String, title: String, content: String, replyTo: Option[String] = None): Unit
}

class DefaultMailer(
  configuration: Configuration
) extends Mailer {
  private val mailerActor = MailerActor.emailActor(configuration)

  override def send(email: String, title: String, content: String, replyTo: Option[String] = None): Unit = {
    mailerActor ! EmailMessage(
      subject = title,
      recipient = email,
      html = Some(content),
      text = Some(content),
      replyTo = replyTo
    )
  }
}

class ConsoleMailer extends Mailer {
  override def send(email: String, title: String, content: String, replyTo: Option[String] = None): Unit = {
    println(
      s"""
         |=====
         |Mail start
         |To: $email
         |Title: $title
         |Content: $content
         |Reply to: ${replyTo.getOrElse("<none>")}
         |Mail end
         |=====
      """.stripMargin)
  }
}
