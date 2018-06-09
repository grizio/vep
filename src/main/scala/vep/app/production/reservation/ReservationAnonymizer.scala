package vep.app.production.reservation

import akka.actor.ActorSystem
import com.typesafe.scalalogging.Logger
import vep.app.production.company.show.play.{Play, PlayService}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ReservationAnonymizer(
  playService: PlayService,
  reservationService: ReservationService,
  actorSystem: ActorSystem,
  executionContext: ExecutionContext
) {
  private implicit val ec: ExecutionContext = executionContext
  private val logger = Logger(getClass)

  def start(): Unit = {
    actorSystem.scheduler.schedule(1.second, 1.day) {
      logger.info("Processing anonymizer")
      process()
      logger.info("Anonymizer processed")
    }
  }

  private def process(): Unit = {
    playService.findToAnonymize()
      .foreach(anonymizePlay)
  }

  private def anonymizePlay(play: Play): Unit = {
    logger.info(s"Anonymize ${play.id}")
    val reservationsToAnonymize = reservationService.findAllByPlay(play.id)
    val anonymizedReservations = reservationsToAnonymize.map(anonymize)
    val anonymizedPlay = play.copy(anonymized = true)
    anonymizedReservations.foreach(reservationService.update)
    playService.update(anonymizedPlay)
  }

  private def anonymize(reservation: Reservation): Reservation = {
    reservation.copy(
      firstName = "xxx",
      lastName = "xxx",
      email = "xxx@xxx.xxx"
    )
  }
}
