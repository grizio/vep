package vep.router

import spray.routing.{HttpService, Route}

trait VepApiRouter extends HttpService with VepRouter {
  override val route: Route = {
    // TODO: used for compilation
    path("") {
      get {
        ctx => ctx.complete("")
      }
    }
}
