package vep.app.seo.views

import akka.http.scaladsl.model.{ContentTypes, HttpResponse}
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.common.page.PageService
import vep.app.production.company.CompanyService
import vep.app.production.company.show.ShowService
import vep.app.production.company.show.play.PlayService
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.collection.immutable
import scala.concurrent.ExecutionContext

class ViewsRouter(
  companyService: CompanyService,
  showService: ShowService,
  playService: PlayService,
  pageService: PageService,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    home ~ page ~ show ~ play
  }

  def home: Route = pathEndOrSingleSlash {
    found(pageService.find("home")) { page =>
      OkView(PageView.view(page))
    }
  }

  def page: Route = publicGet("page" / Segment) { canonical =>
    found(pageService.find(canonical)) { page =>
      OkView(PageView.view(page))
    }
  }

  def show: Route = publicGet("production" / "companies" / Segment / "shows" / "page" / Segment) { (companyId, showId) =>
    found(companyService.find(companyId)) { company =>
      found(showService.findFromCompanyWithDependencies(company, showId)) { show =>
        OkView(ShowView.view(show))
      }
    }
  }

  def play: Route = publicGet("production" / "companies" / Segment / "shows" / Segment / "plays" / "page" / Segment) { (companyId, showId, playId) =>
    found(companyService.find(companyId)) { company =>
      found(showService.findFromCompany(company, showId)) { show =>
        found(playService.findFromShowWithDependencies(show, playId)) { play =>
          OkView(PlayView.view(play, playService.findAllFromShow(show)))
        }
      }
    }
  }

  private def OkView(content: String): HttpResponse = {
    Ok(
      entity = content,
      headers = immutable.Seq(`Content-Type`(ContentTypes.`text/html(UTF-8)`))
    )
  }
}
