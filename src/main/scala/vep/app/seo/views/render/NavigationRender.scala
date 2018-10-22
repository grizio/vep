package vep.app.seo.views.render

import vep.app.common.page.{Page, PageService}
import vep.app.production.company.show.{ShowMeta, ShowService}
import vep.app.production.company.show.play.{PlayMeta, PlayService}

import scala.xml.{Node, NodeSeq}

trait NavigationRender {
  self: Render =>

  def showService: ShowService

  def playService: PlayService

  def pageService: PageService

  def renderNavigation(): NodeSeq = {
    val shows = showService.findNext()
    val plays = playService.findNext()
    val pages = pageService.findAll()

    <div class="left-navigation">
      {renderSitename}
      {renderNav(shows, plays, pages)}
    </div>
  }

  private def renderSitename: NodeSeq = {
    <div class="sitename">
      <a href="/" class="row middle center">
        <img src="/assets/logo-acve.png" alt="Logo de Voir &amp; entendre" class="col"/>
        <span class="col">Voir&nbsp;&amp;&nbsp;entendre</span>
      </a>
    </div>
  }

  private def renderNav(
    shows: Seq[ShowMeta],
    plays: Seq[PlayMeta],
    pages: Seq[Page]
  ): NodeSeq = {
    <nav>
      {renderEmptyMenuGroup("Accueil", "/")}
      {renderNextShows(shows)}
      {renderNextPlays(plays)}
      {renderPages(pages)}
      {renderEmptyMenuGroup("Nous contacter", "/contact")}
      {renderEmptyMenuGroup("Carte du site", "/sitemap")}
    </nav>
  }

  def renderNextShows(shows: Seq[ShowMeta]): NodeSeq = {
    if (shows.nonEmpty) {
      renderMenuGroup("Les prochains spectacles", "/shows") {
        shows.map { show =>
          renderMenuItem(show.title, seo.showUrl(show))
        }
      }
    } else {
      renderEmptyMenuGroup("Les prochains spectacles", "/shows")
    }
  }

  def renderNextPlays(plays: Seq[PlayMeta]): NodeSeq = {
    if (plays.nonEmpty) {
      renderMenuGroup("Les prochaines séances", "/production/plays") {
        plays.map { play =>
          renderMenuItem(
            seo.playTitle(play),
            seo.playUrl(play)
          )
        }
      }
    } else {
      renderEmptyMenuGroup("Les prochaines séances", "/production/plays")
    }
  }

  def renderPages(pages: Seq[Page]): NodeSeq = {
    if (pages.nonEmpty) {
      val filteredPages = pages.filter(_.order > 0)
      val sortedPages = filteredPages.sortBy(_.order)
      renderMenuGroup("L'association", "/page/association") {
        sortedPages.map(page =>
          renderMenuItem(
            seo.pageTitle(page),
            seo.pageUrl(page)
          )
        )
      }
    } else {
      renderEmptyMenuGroup("L'association", "/page/association")
    }
  }

  private def renderEmptyMenuGroup(name: String, href: String): NodeSeq = renderMenuGroup(name, href)(NodeSeq.Empty)

  private def renderMenuGroup(name: String, href: String)(children: => NodeSeq): NodeSeq = {
    <div class="menu-group">
      <a href={href}>{name}</a>
      <div class="menu-items">
        {children}
      </div>
    </div>
  }

  private def renderMenuItem(name: String, href: String): Node = {
    <div class="menu-item">
      <a href={href}>{name}</a>
    </div>
  }
}
