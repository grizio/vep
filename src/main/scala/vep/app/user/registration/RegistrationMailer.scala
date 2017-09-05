package vep.app.user.registration

import vep.Configuration
import vep.app.user.User
import vep.framework.mailer.Mailer

class RegistrationMailer(
  mailer: Mailer,
  configuration: Configuration
) {
  def send(user: User): Unit = {
    mailer.send(
      user.email,
      "Inscription sur le site de Voir & Entendre",
      content(user)
    )
  }

  private def content(user: User): String = {
    val activationUrl = s"${configuration.server.public}/api/user/activation/${user.email}/${user.activationKey.getOrElse("")}"
    val contactUrl = s"${configuration.server.public}/contact"
    s"""
       |<p>
       |  Bonjour,
       |</p>
       |
       |<p>
       |  Vous venez de vous inscrire sur le site de Voir &amp; Entendre.
       |  Afin de compléter votre inscription, veuillez accéder au lien suivant :
       |</p>
       |
       |<p style="font-size: 1.5rem">
       |  <a href="$activationUrl">$activationUrl</a>
       |</p>
       |
       |<p>
       |  Voir &amp; Entendre ne vous demandera jamais vos identifiants.
       |  Si vous recevez un mail en notre nom vous demandant vos identifiants,
       |  veuillez ignorer le mail et <a href="${contactUrl}">nous contacter</a>,
       |  il s'agit surement d'une tentative de phishing.
       |  De même, Voir &amp; Entendre ne vous demandera jamais vos informations bancaires.
       |  Si vous avez le moindre doute, n'hésitez pas à <a href="${contactUrl}">nous contacter</a>.
       |</p>
       |
       |<p>
       |  Voir &amp; Entendre
       |</p>
    """.stripMargin
  }
}
