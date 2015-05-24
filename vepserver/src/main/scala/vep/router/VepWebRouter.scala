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

  lazy val end = {
    ctx: RequestContext => ctx.complete(404, "Not Found")
  }

  override lazy val route: Route = log {
    pathPrefix("public") {
      getFromResourceDirectory("public") ~ end
    } ~
      pathPrefix("packages") {
        getFromResourceDirectory("packages") ~ end
      } ~
      path("favicon.ico") {
        getFromResource("favicon.ico") ~ end
      } ~
      path("robots.txt") {
        getFromResource("robots.txt") ~ end
      } ~
      path("main.js") {
        getFromResource("main.js") ~ end
      } ~
      path(Segments) { seg =>
        getFromResource("index.html")
      }
  }
}
