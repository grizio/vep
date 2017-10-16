package vep.app

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import vep.app.common.CommonIntegrationModule
import vep.app.production.ProductionIntegrationModule
import vep.app.seo.SeoIntegrationModule
import vep.app.user.UserIntegrationModule
import vep.framework.database.DatabaseConnection
import vep.{Configuration, Environment}
import vep.framework.mailer.{ConsoleMailer, DefaultMailer, Mailer}
import vep.framework.router.CorsHelper

import scala.concurrent.ExecutionContext

trait AppIntegrationModule
  extends UserIntegrationModule
    with CommonIntegrationModule
    with ProductionIntegrationModule
    with SeoIntegrationModule
    with AppRouter
    with DatabaseConnection {
  def executionContext: ExecutionContext

  lazy val configuration = new Configuration()
  lazy val mailer: Mailer = configuration.environment match {
    case Environment.prod => new DefaultMailer(configuration)
    case _ => new ConsoleMailer
  }
}

trait AppRouter {
  self: AppIntegrationModule =>

  lazy val route: Route = encodeResponse {
    apiRoute ~ seoRoute ~ clientRouter.route
  }

  lazy val apiRoute: Route = {
    val route = pathPrefix("api") {
      commonRoute ~ userRoute ~ productionRoute
    }
    if (configuration.environment == Environment.dev) {
      CorsHelper.withCors(route)
    } else {
      route
    }
  }

  lazy val clientRouter = new ClientRouter
}
