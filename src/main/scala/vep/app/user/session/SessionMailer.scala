package vep.app.user.session

import vep.Configuration
import vep.framework.mailer.Mailer

class SessionMailer(
  mailer: Mailer,
  configuration: Configuration
) {
  def sendResetToken(email: String, token: String): Unit = {
    mailer.send(
      email,
      "Récupération de votre mot de passe",
      contentResetToken(email, token)
    )
  }

  private def contentResetToken(email: String, token: String): String = {
    val resetUrl = s"${configuration.server.public}/personal/password/reset?email=${email}&token=${token}"
    val contactUrl = s"${configuration.server.public}/contact"
    s"""
       |<p>
       |  Bonjour,
       |</p>
       |
       |<p>
       |  Vous venez de faire une demande de récupération de votre mot de passe.
       |  Pour ce faire, veuillez accéder à l'adresse suivante :
       |</p>
       |
       |<p style="font-size: 1.5rem">
       |  <a href="$resetUrl">$resetUrl</a>
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
