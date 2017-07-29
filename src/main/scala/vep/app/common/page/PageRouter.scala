package vep.app.common.page

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class PageRouter(
  pageVerifications: PageVerifications,
  pageService: PageService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAll ~ create
  }

  def findAll: Route = publicGet("pages") {
    Ok(pageService.findAll())
  }

  def create: Route = adminPost("pages" / Segment, as[Page]).apply { (canonical, page, _) =>
    verifying(pageVerifications.verify(page, canonical)) { validPage =>
      verifying(pageService.create(validPage)) { savedPage =>
        Ok(savedPage)
      }
    }
  }
}
