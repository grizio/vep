package vep.app.production.reservation

import vep.Configuration
import vep.app.production.company.show.play.PlayWithDependencies
import vep.framework.mailer.Mailer
import vep.framework.utils.DateUtils

class ReservationMailer(
  mailer: Mailer,
  configuration: Configuration
) {
  def send(reservation: Reservation, play: PlayWithDependencies): Unit = {
    mailer.send(
      reservation.email,
      "Inscription sur le site de Voir & Entendre",
      content(reservation, play),
      replyTo = Some(configuration.email.replyTo)
    )
  }

  private def content(reservation: Reservation, play: PlayWithDependencies): String = {
    s"""
      |<p>
      |    Madame, Monsieur,
      |</p>
      |<p>
      |    Merci pour votre réservation sur le site de Voir &amp; Entendre.
      |</p>
      |
      |<p>
      |    Vous trouverez ci-dessous le détail :
      |</p>
      |
      |<ul>
      |    <li>Date : ${DateUtils.longDate(play.play.date)}</li>
      |    <li>Salle : ${play.theater.name} (${play.theater.address})</li>
      |    <li>Spectacle(s) : ${play.show.title} par ${play.company.name}</li>
      |    <li>Nom : ${reservation.firstName} ${reservation.lastName}</li>
      |    <li>Places réservées : ${reservation.seats.mkString(", ")}</li>
      |</ul>
      |
      |<p>
      |    Merci de vous présenter au plus tard 15 minutes avant le début de la séance pour récupérer vos billets.
      |</p>
      |
      |<p>
      |    Dans le cas contraire, si nécessaire, les places peuvent être redistribuées à d'autres spectateurs.
      |</p>
      |
      |<p>
      |    Pour toute information, nous vous invitons à répondre directement à ce mail.
      |</p>
      |
      |<p>
      |    Vous souhaitant bonne réception,
      |    <br/>
      |    L'équipe Voir &amp; Entendre
      |</p>
    """.stripMargin
  }
}
