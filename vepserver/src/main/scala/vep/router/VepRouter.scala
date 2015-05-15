package vep.router

import spray.routing._


trait VepRouter {
  val route: Route
}