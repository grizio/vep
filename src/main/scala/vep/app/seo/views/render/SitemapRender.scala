package vep.app.seo.views.render

import vep.app.common.page.Page
import vep.app.production.company.show.ShowWithDependencies

import scala.xml.NodeSeq

trait SitemapRender extends Render {
  type Link = (String, String)
  type Links = Seq[Link]

  def renderSitemap(
    pages: Seq[Page],
    shows: Seq[ShowWithDependencies]
  ): String = render("Carte du site") {
    <header>Carte du site</header>
    <section>
      <div class="">
        <div>
          {renderInformation(pages)}
          {renderShows(shows)}
        </div>
      </div>
    </section>
  }

  private def renderInformation(pages: Seq[Page]): NodeSeq = {
    <section>
      <h2>Information</h2>
      {renderLinks(pages.map(page => seo.pageUrl(page) -> seo.pageTitle(page)))}
    </section>
  }

  private def renderShows(shows: Seq[ShowWithDependencies]): NodeSeq = {
    <section>
      <h2>Pièces</h2>

      {shows.map(renderShow)}
    </section>
  }

  private def renderShow(show: ShowWithDependencies): NodeSeq = {
    val showLink = seo.showUrl(show) -> seo.showTitle(show)
    val playLinks = show.plays.map { play =>
      seo.playUrl(show, play) -> seo.playTitle(play)
    }

    <div>
      <h3>{show.show.title}</h3>
      {renderLinks(showLink +: playLinks)}
    </div>
  }

  private def renderLinks(links: Links): NodeSeq = {
    <ul>
      {links.map(renderLink)}
    </ul>
  }

  private def renderLink(link: Link): NodeSeq = {
    <li><a href={link._1}>{link._2}</a></li>
  }
}
