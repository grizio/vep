package vep.app.seo.sitemap

import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.common.page.PageService
import vep.app.production.company.show.{ShowService, ShowWithDependencies}
import vep.app.seo.SeoCommon
import vep.app.user.UserService
import vep.framework.router.RouterComponent
import vep.framework.utils.DateUtils

import scala.collection.immutable
import scala.concurrent.ExecutionContext
import scala.xml.{Elem, Utility}

class SitemapRouter(
  showService: ShowService,
  pageService: PageService,
  seoCommon: SeoCommon,
  val configuration: Configuration,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    sitemap
  }

  def sitemap: Route = publicGet("sitemaps") {
    println(Utility.trim(buildSitemap()).buildString(stripComments = true))
    Ok(
      entity = s"""<?xml version="1.0" encoding="UTF-8"?>${Utility.trim(buildSitemap()).buildString(stripComments = true)}""",
      headers = immutable.Seq(`Content-Type`(ContentTypes.`text/xml(UTF-8)`))
    )
  }

  private def buildSitemap(): Elem = {
    val sitemapUrls = Seq(homepage) ++ shows ++ pages
    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
       {sitemapUrls.map(_.toElem)}
    </urlset>
  }

  private def homepage = SitemapUrl(
    url = seoCommon.homeUrl,
    changeFrequency = Some(ChangeFrequency.weekly),
    priority = Some(1)
  )

  private def shows: Seq[SitemapUrl] = {
    showService.findAllWithDependencies()
      .flatMap(sitemapShow)
  }

  private def sitemapShow(show: ShowWithDependencies): Seq[SitemapUrl] = {
    val hasPastPlays = show.plays.exists(play => DateUtils.isBeforeNow(play.date))
    val hasFuturePlays = show.plays.exists(play => DateUtils.isAfterNow(play.date))
    val baseSitemap =
      if (hasPastPlays && hasFuturePlays) baseSitemapCurrentShow
      else if (hasFuturePlays) baseSitemapFutureShow
      else baseSitemapPastShow // No difference in strategy for past shows and no shows

    val showSitemap = baseSitemap.copy(
      url = seoCommon.showUrl(show)
    )
    val playsSitemap = show.plays.filter(play => DateUtils.isAfterNow(play.date)).map { play =>
      baseSitemap.copy(
        url = seoCommon.playUrl(show, play),
        priority = baseSitemap.priority.map(_ - 0.1)
      )
    }
    showSitemap +: playsSitemap
  }

  private val baseSitemapCurrentShow = SitemapUrl(
    url = "",
    changeFrequency = Some(ChangeFrequency.daily),
    priority = Some(0.9)
  )

  private val baseSitemapFutureShow = SitemapUrl(
    url = "",
    changeFrequency = Some(ChangeFrequency.weekly),
    priority = Some(0.6)
  )

  private val baseSitemapPastShow = SitemapUrl(
    url = "",
    changeFrequency = Some(ChangeFrequency.yearly),
    priority = Some(0.3)
  )

  private def pages: Seq[SitemapUrl] = {
    pageService.findAll()
      .filter(_.canonical != "homepage")
      .map { page =>
        SitemapUrl(
          url = seoCommon.pageUrl(page),
          changeFrequency = Some(ChangeFrequency.monthly),
          priority = Some(0.4)
        )
      }
  }
}
