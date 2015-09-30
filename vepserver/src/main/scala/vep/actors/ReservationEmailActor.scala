package vep.actors

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.ConfigFactory
import vep.FinalVepServicesProductionComponent
import vep.model.session.{ReservationDetail, ReservationPriceDetail, SessionDetail}
import vep.model.show.Show
import vep.model.theater.Theater
import vep.utils.DateUtils

case class ReservationCreate(id: Int)

class ReservationEmailActor extends Actor with ActorLogging with FinalVepServicesProductionComponent with EmailActorHelper {
  lazy val config = ConfigFactory.load()

  override def receive = {
    case ReservationCreate(id) =>
      reservationService.find(id) foreach { reservation =>
        sessionService.findByReservation(id) foreach { session =>
          val shows = session.shows.map(showService.findByCanonical).filter(_.isDefined).map(_.get)
          theaterService.findByCanonical(session.theater) foreach { theater =>
            context.actorSelection("/user/email") ! EmailMessage(
              subject = "Votre réservation sur Voir & Entendre",
              recipient = reservation.email,
              from = "robot@voir-entendre-posso.fr",
              replyTo = Some("contact@voir-entendre-posso.fr"),
              text = Some(fill(getText("reservation-create.txt"), reservation, session, theater, shows, asHTML = false)),
              html = Some(fill(getText("reservation-create.html"), reservation, session, theater, shows, asHTML = true))
            )
          }
        }
      }
    case unexpectedMessage: Any =>
      throw new Exception(s"can't handle $unexpectedMessage")
  }

  def fill(text: String, reservation: ReservationDetail, session: SessionDetail, theater: Theater, shows: Seq[Show], asHTML: Boolean): String = {
    implicit val _asHTML = asHTML
    var result = text
    result = replaceParam(result, "firstName", reservation.firstName)
    result = replaceParam(result, "lastName", reservation.lastName)
    reservation.seats foreach { seats =>
      result = replaceParam(result, "seats", seats + " places")
    }
    if (reservation.seatList.nonEmpty) {
      result = replaceParam(result, "seats", reservation.seatList.mkString(", "))
    }
    result = replaceParam(result, "prices", literalPrices(reservation.prices, session) + " = " + totalPrices(reservation.prices) + "€")
    result = replaceParam(result, "address", config.getString("vep.contact.address"))
    result = replaceParam(result, "date", DateUtils.toStringDisplayLong(session.date))
    result = replaceParam(result, "theaterName", theater.name)
    result = replaceParam(result, "theaterAddress", theater.address)
    result = replaceParam(result, "shows", shows.map(_.title).mkString(", "))
    result
  }

  def literalPrices(prices: Seq[ReservationPriceDetail], session: SessionDetail) = prices map { p =>
    val priceName = session.prices.find(sp => sp.id == p.price).map("(" + _.name + ")").getOrElse("")
    s"${p.number} * ${p.value / 100}€ $priceName"
  } mkString " + "

  def totalPrices(prices: Seq[ReservationPriceDetail]) = prices.foldLeft(0)((r, p) => r + p.number * p.value / 100)
}
