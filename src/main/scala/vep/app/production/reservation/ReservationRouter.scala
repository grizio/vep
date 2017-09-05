package vep.app.production.reservation

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.production.company.show.play.PlayService
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class ReservationRouter(
  reservationVerifications: ReservationVerifications,
  reservationService: ReservationService,
  reservationMailer: ReservationMailer,
  playService: PlayService,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findReservedSeats ~ create ~ findByplay ~ delete
  }

  def findReservedSeats: Route = publicGet("production" / "reservations" / Segment / "reservedSeats") { (playId) =>
    found(playService.find(playId)) { play =>
      Ok(reservationService.findReservedSeats(play.id))
    }
  }

  def create: Route = publicPost("production" / "reservations" / Segment, as[ReservationCreation]).apply { (playId, reservationCreation) =>
    found(playService.find(playId)) { play =>
      verifying(reservationVerifications.verifyCreation(reservationCreation, play)) { reservation =>
        verifying(reservationService.create(reservation, playId)) { reservation =>
          playService.findWithDependencies(playId).foreach { playWithDependencies =>
            reservationMailer.send(reservation, playWithDependencies)
          }
          Ok(reservation)
        }
      }
    }
  }

  def findByplay: Route = adminGet("production" / "reservations" / Segment).apply { (playId, _) =>
    found(playService.find(playId)) { play =>
      Ok(reservationService.findAllByPlay(play.id))
    }
  }

  def delete: Route = adminDelete("production" / "reservations" / Segment / Segment).apply { (playId, reservationId, _) =>
    found(playService.find(playId)) { play =>
      verifying(reservationService.delete(play.id, reservationId)) { _ =>
        Ok("")
      }
    }
  }
}