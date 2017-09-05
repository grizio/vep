package vep.app.common.page

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class PageRouter(
  pageVerifications: PageVerifications,
  pageService: PageService,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findAll ~ find ~ create ~ update
  }

  def findAll: Route = publicGet("pages") {
    Ok(pageService.findAll())
  }

  def find: Route = publicGet("pages" / Segment) { canonical =>
    Ok(pageService.find(canonical))
  }

  def create: Route = adminPost("pages" / Segment, as[Page]).apply { (canonical, page, _) =>
    verifying(pageVerifications.verify(page, canonical)) { validPage =>
      verifying(pageService.create(validPage)) { savedPage =>
        Ok(savedPage)
      }
    }
  }

  def update: Route = adminPut("pages" / Segment, as[Page]).apply { (canonical, page, _) =>
    found(pageService.find(canonical)) { _ =>
      verifying(pageVerifications.verifyUpdate(page, canonical)) { validPage =>
        verifying(pageService.update(validPage)) { savedPage =>
          Ok(savedPage)
        }
      }
    }
  }
}
