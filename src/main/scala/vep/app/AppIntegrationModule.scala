package vep.app

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import vep.{Configuration, Environment}
import vep.framework.router.CorsHelper

import scala.concurrent.ExecutionContext

trait AppIntegrationModule
  extends AppRouter {
  def executionContext: ExecutionContext

  lazy val configuration = new Configuration()
}

trait AppRouter {
  self: AppIntegrationModule =>

  lazy val route: Route = apiRoute ~ clientRouter.route

  lazy val apiRoute: Route = {
    val route = pathPrefix("api") {
      complete("It works!")
    }
    if (configuration.environment == Environment.dev) {
      CorsHelper.withCors(route)
    } else {
      route
    }
  }

  lazy val clientRouter = new ClientRouter
}
