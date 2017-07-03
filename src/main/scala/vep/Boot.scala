package vep

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import vep.evolution.database.DatabaseEvolution
import vep.router.Router

import scala.io.StdIn

object Boot extends App {
  val logger = Logger(Boot.getClass)

  val configuration = new Configuration()
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val router = new Router()

  new DatabaseEvolution(configuration).setup()

  val bindingFuture = Http().bindAndHandle(router.route, configuration.server.host, configuration.server.port)

  logger.info(s"Server online at http://${configuration.server.host}:${configuration.server.port}")

  if (configuration.environment == Environment.dev) {
    logger.info("Press RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
