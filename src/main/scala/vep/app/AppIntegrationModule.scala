package vep.app

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import vep.app.common.CommonVerifications
import vep.app.user.UserIntegrationModule
import vep.{Configuration, Environment}
import vep.framework.mailer.{ConsoleMailer, DefaultMailer, Mailer}
import vep.framework.router.CorsHelper

import scala.concurrent.ExecutionContext

trait AppIntegrationModule
  extends UserIntegrationModule
    with AppRouter {
  def executionContext: ExecutionContext

  lazy val configuration = new Configuration()
  lazy val mailer: Mailer = configuration.environment match {
    case Environment.prod => new DefaultMailer(configuration)
    case _ => new ConsoleMailer
  }

  lazy val commonVerifications = new CommonVerifications()
}

trait AppRouter {
  self: AppIntegrationModule =>

  lazy val route: Route = apiRoute ~ clientRouter.route

  lazy val apiRoute: Route = {
    val route = pathPrefix("api") {
      userRoute
    }
    if (configuration.environment == Environment.dev) {
      CorsHelper.withCors(route)
    } else {
      route
    }
  }

  lazy val clientRouter = new ClientRouter
}
