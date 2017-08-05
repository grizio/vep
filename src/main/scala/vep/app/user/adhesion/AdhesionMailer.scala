package vep.app.user.adhesion

import vep.Configuration
import vep.framework.mailer.Mailer

class AdhesionMailer(
  mailer: Mailer,
  configuration: Configuration
) {
  def sendRefused(adhesion: AdhesionView, reason: String): Unit = {
    mailer.send(
      adhesion.user.email,
      "Refus de votre demande d'adhésion",
      contentRefused(adhesion, reason)
    )
  }

  private def contentRefused(adhesion: AdhesionView, reason: String): String = {
    s"""
       |<p>
       |  Bonjour,
       |</p>
       |
       |<p>
       |  Votre demande d'adhésion a été refusée.
       |  Le motif fourni est le suivant :
       |</p>
       |
       |<p>
       |  ${reason}
       |</p>
       |
       |<p>
       |  Vous pouvez cependant toujours faire une nouvelle demande tant que les inscriptions sont ouvertes.
       |  Si vous avez des questions, n'hésitez pas à répondre à cet email.
       |</p>
       |
       |<p>
       |  Voir &amp; Entendre
       |</p>
    """.stripMargin
  }
}
