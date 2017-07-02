package vep.router

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._

class Router {
  private val apiRouter = new ApiRouter
  private val clientRouter = new ClientRouter

  def route: Route = apiRouter.route ~ clientRouter.route
}
