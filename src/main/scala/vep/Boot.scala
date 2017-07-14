package vep

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import vep.app.AppIntegrationModule
import vep.evolution.database.DatabaseEvolution

import scala.io.StdIn

object Boot extends App with AppIntegrationModule {
  val logger = Logger(Boot.getClass)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  new DatabaseEvolution(configuration).setup()

  val bindingFuture = Http().bindAndHandle(route, configuration.server.host, configuration.server.port)

  logger.info(s"Server online at ${configuration.server.protocol}://${configuration.server.host}:${configuration.server.port}")

  if (configuration.environment == Environment.dev) {
    logger.info("Press RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
