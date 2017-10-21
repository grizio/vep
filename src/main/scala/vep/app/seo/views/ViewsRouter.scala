package vep.app.seo.views

import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.model.{ContentTypes, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.common.page.PageService
import vep.app.production.company.CompanyService
import vep.app.production.company.show.ShowService
import vep.app.production.company.show.play.PlayService
import vep.app.seo.SeoCommon
import vep.app.seo.views.render.{PageRender, PlayRender, ShowRender, SitemapRender}
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.collection.immutable
import scala.concurrent.ExecutionContext

class ViewsRouter(
  companyService: CompanyService,
  showService: ShowService,
  playService: PlayService,
  pageService: PageService,
  val seo: SeoCommon,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent
  with PageRender
  with PlayRender
  with ShowRender
  with SitemapRender {
  lazy val route: Route = {
    home ~ page ~ show ~ play ~ sitemap
  }

  def home: Route = pathEndOrSingleSlash {
    optionalHeaderValueByName("User-Agent") { userAgent =>
      found(pageService.find("home")) { page =>
        OkView(renderPage(page), userAgent)
      }
    }
  }

  def page: Route = publicGet("page" / Segment) { canonical =>
    optionalHeaderValueByName("User-Agent") { userAgent =>
      found(pageService.find(canonical)) { page =>
        OkView(renderPage(page), userAgent)
      }
    }
  }

  def show: Route = publicGet("production" / "companies" / Segment / "shows" / "page" / Segment) { (companyId, showId) =>
    optionalHeaderValueByName("User-Agent") { userAgent =>
      found(companyService.find(companyId)) { company =>
        found(showService.findFromCompanyWithDependencies(company, showId)) { show =>
          OkView(renderShow(show), userAgent)
        }
      }
    }
  }

  def play: Route = publicGet("production" / "companies" / Segment / "shows" / Segment / "plays" / "page" / Segment) { (companyId, showId, playId) =>
    optionalHeaderValueByName("User-Agent") { userAgent =>
      found(companyService.find(companyId)) { company =>
        found(showService.findFromCompany(company, showId)) { show =>
          found(playService.findFromShowWithDependencies(show, playId)) { play =>
            OkView(renderPlay(play, playService.findAllFromShow(show)), userAgent)
          }
        }
      }
    }
  }

  // Not sitemaps, kept for search engines
  def sitemap: Route = publicGet("sitemap") {
    optionalHeaderValueByName("User-Agent") { userAgent =>
      val pages = pageService.findAll()
      val shows = showService.findAllWithDependencies()
      OkView(renderSitemap(pages, shows), userAgent)
    }
  }

  private def OkView(content: String, userAgent: Option[String]): HttpResponse = {
    val normalizedContent = if (userAgent.exists(isBot)) {
      content.replaceAll("<script(.*)</script>", "")
    } else {
      content
    }
    Ok(
      entity = normalizedContent,
      headers = immutable.Seq(`Content-Type`(ContentTypes.`text/html(UTF-8)`))
    )
  }

  private def isBot(userAgent: String): Boolean = {
    val userAgentBots = Seq("bot", "crawl", "slurp", "spider", "mediapartners")
    userAgentBots.exists(userAgentBot => userAgent.toLowerCase().contains(userAgentBot))
  }
}
