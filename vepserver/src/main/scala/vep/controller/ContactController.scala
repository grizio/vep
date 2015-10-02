package vep.controller

import akka.actor.ActorRefFactory
import com.typesafe.config.ConfigFactory
import vep.actors.EmailMessage
import vep.model.cms.Contact
import vep.model.common.{ResultErrors, ResultSuccess}
import vep.service.VepServicesComponent
import vep.utils.EmailUtils

trait ContactControllerComponent {
  def contactController: ContactController

  trait ContactController {
    def post(contact: Contact)(implicit actorRefFactory: ActorRefFactory): Either[ResultErrors, ResultSuccess]
  }

}

trait ContactControllerProductionComponent extends ContactControllerComponent {
  self: VepServicesComponent =>

  override val contactController = new ContactControllerProduction

  class ContactControllerProduction extends ContactController {
    lazy val config = ConfigFactory.load()

    override def post(contact: Contact)(implicit actorRefFactory: ActorRefFactory): Either[ResultErrors, ResultSuccess] = {
      if (contact.verify) {
        actorRefFactory.actorSelection("/user/email") ! EmailMessage(
          subject = s"Contact : ${contact.title}",
          recipient = s"Voir & Entendre <${config.getString("vep.contact.email")}>",
          replyTo = Some(s"${contact.name} <${contact.email}>"),
          text = Some(fill(EmailUtils.getText("contact.txt"), contact, asHTML = false)),
          html = Some(fill(EmailUtils.getText("contact.html"), contact, asHTML = true))
        )
        Right(ResultSuccess)
      } else {
        Left(contact.toResult.asInstanceOf[ResultErrors])
      }
    }

    def fill(text: String, contact: Contact, asHTML: Boolean): String = {
      EmailUtils.replaceParams(text)(
        "name" -> contact.name,
        "email" -> contact.email,
        "title" -> contact.title,
        "content" -> contact.content
      )(asHTML)
    }
  }

}
