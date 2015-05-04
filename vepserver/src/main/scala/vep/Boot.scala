package vep

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem()

  val handlerWeb = system.actorOf(Props[VepWebActor], name = "vep-web-service")
  val handlerApi = system.actorOf(Props[VepApiActor], name = "vep-api-service")

  IO(Http) ! Http.Bind(handlerWeb, interface = "vep.test", port = 8080)
  IO(Http) ! Http.Bind(handlerApi, interface = "vep.test", port = 8081)
}
