package vep

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import vep.router.Router

import scala.io.StdIn

object Boot extends App {
  val configuration = new Configuration()
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val router = new Router()

  val bindingFuture = Http().bindAndHandle(router.route, configuration.server.host, configuration.server.port)
  println(s"Server online at http://${configuration.server.host}:${configuration.server.port}")
  println("Press RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
