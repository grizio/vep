package vep.app.seo

import vep.Configuration
import vep.app.common.page.Page
import vep.app.production.company.show.ShowWithDependencies
import vep.app.production.company.show.play.{PlayView, PlayWithDependencies}
import vep.framework.utils.{DateUtils, StringUtils}

class SeoCommon(
  configuration: Configuration
) {
  def homeUrl: String = {
    url("/")
  }

  def pageUrl(page: Page): String = {
    url(s"/page/${page.canonical}")
  }

  def pageTitle(page: Page): String = {
    page.title
  }

  def showUrl(show: ShowWithDependencies): String = {
    url(s"/production/companies/${show.company.id}/shows/page/${show.show.id}")
  }

  def showTitle(show: ShowWithDependencies): String = {
    show.show.title
  }

  def playUrl(show: ShowWithDependencies, play: PlayView): String = {
    url(s"/production/companies/${show.company.id}/shows/${show.show.id}/plays/page/${play.id}")
  }

  def playTitle(play: PlayWithDependencies): String = {
    s"${StringUtils.capitalizeFirstLetter(DateUtils.longDate(play.play.date))} • ${play.show.title}"
  }

  def playTitle(play: PlayView): String = {
    s"${StringUtils.capitalizeFirstLetter(DateUtils.longDate(play.date))}"
  }

  private def url(path: String): String = {
    s"${configuration.server.public}${StringUtils.forceStartingWith(path, "/")}"
  }
}
