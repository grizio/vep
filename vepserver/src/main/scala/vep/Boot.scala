package vep

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http
import vep.actors.{EmailServiceActor, ReservationEmailActor}

object Boot extends App {
  implicit val system = ActorSystem()
  val config = ConfigFactory.load()

  val handlerWeb = system.actorOf(Props[VepWebActor], name = config.getString("vep.web.name"))
  val handlerApi = system.actorOf(Props[VepApiActor], name = config.getString("vep.api.name"))
  val emailActor = system.actorOf(Props[EmailServiceActor], name = "email")
  val reservationEmailActor = system.actorOf(Props[ReservationEmailActor], name = "reservationEmailActor")

  IO(Http) ! Http.Bind(handlerWeb, interface = config.getString("vep.web.interface"), port = config.getInt("vep.web.port"))
  IO(Http) ! Http.Bind(handlerApi, interface = config.getString("vep.api.interface"), port = config.getInt("vep.api.port"))
}
