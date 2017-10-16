package vep.app.seo.sitemap

import org.joda.time.DateTime

import scala.xml.{Elem, NodeSeq}

case class SitemapUrl(
  url: String,
  lastModification: Option[DateTime] = None,
  changeFrequency: Option[ChangeFrequency.Value] = None,
  priority: Option[Double] = None
) {
  def toElem: Elem = {
    <url>
      <loc>{url}</loc>
      {optElem(lastModification.map(lastModification => <lastmod>{lastModification}</lastmod>))}
      {optElem(changeFrequency.map(changeFrequency => <changefreq>{changeFrequency}</changefreq>))}
      {optElem(priority.map(priority => <priority>{priority}</priority>))}
    </url>
  }

  def optElem(elemOpt: Option[Elem]): NodeSeq = {
    elemOpt match {
      case Some(elem) => NodeSeq.fromSeq(elem)
      case None => NodeSeq.Empty
    }
  }
}

object ChangeFrequency extends Enumeration {
  val always, hourly, daily, weekly, monthly, yearly, never = Value
}