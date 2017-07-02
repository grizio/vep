package vep.router

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._

class ApiRouter {
  def route: Route = pathPrefix("api") { ctx =>
    ctx.complete("api")
  }
}
