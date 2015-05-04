package vep.router

import java.util.logging.Logger

import spray.routing._
import spray.routing.directives.DebuggingDirectives

trait VepWebRouter extends HttpService with VepRouter {
  val logger = Logger.getLogger("RouterV1")

  def log[T]: Directive0 = mapRequestContext { ctx =>
    logger.info(ctx.request.uri.toString())
    ctx
  }

  DebuggingDirectives.logRequest("vep-web-service")

  override lazy val route: Route = log {
    pathPrefix("public") {
      getFromResourceDirectory("public")
    } ~
      pathPrefix("packages") {
        getFromResourceDirectory("packages")
      } ~
      path("favicon.ico") {
        getFromResource("favicon.ico")
      } ~
      path("robots.txt") {
        getFromResource("robots.txt")
      } ~
      path("main.js") {
        getFromResource("main.js")
      } ~
      path(Segments) { seg =>
        getFromResource("index.html")
      }
  }
}
