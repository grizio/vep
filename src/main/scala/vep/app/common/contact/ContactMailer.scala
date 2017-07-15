package vep.app.common.contact

import vep.Configuration
import vep.framework.mailer.Mailer

class ContactMailer(
  mailer: Mailer,
  configuration: Configuration
) {
  def send(contact: Contact): Unit = {
    mailer.send(
      title = s"Contact : ${contact.title}",
      email = s"Voir & Entendre <${configuration.email.replyTo}>",
      replyTo = Some(s"${contact.name} <${contact.email}>"),
      content = content(contact)
    )
  }

  private def content(contact: Contact): String = {
    s"""
      |<p>
      |  Vous avez reçu un e-mail de ${contact.name} (${contact.email}) rédigé depuis le site Voir &amp; Entendre.
      |  Vous trouverez ci-dessous le contenu de son message.
      |  Pour répondre à l'expéditeur, utilisez simplement la fonction « répondre ».
      |</p>
      |
      |<h3>${contact.title}</h3>
      |
      |<p>${contact.content}</p>
     """.stripMargin
  }
}
