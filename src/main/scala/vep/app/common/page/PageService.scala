package vep.app.common.page

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class PageService(
  val configuration: Configuration
) extends DatabaseContainer {
  def findAll(): Seq[Page] = withQueryConnection { implicit session =>
    findAllPages()
  }

  private def findAllPages()(implicit session: DBSession): Seq[Page] = {
    sql"""
      SELECT * FROM page
    """
      .map(Page.apply)
      .list()
      .apply()
  }

  def find(canonical: String): Option[Page] = withQueryConnection { implicit session =>
    findPage(canonical)
  }

  private def findPage(canonical: String)(implicit session: DBSession): Option[Page] = {
    sql"""
      SELECT * FROM page
      WHERE canonical = ${canonical}
    """
      .map(Page.apply)
      .single()
      .apply()
  }

  def create(page: Page): Validation[Page] = withCommandTransaction { implicit session =>
    insertPage(page)
    Valid(page)
  }

  private def insertPage(page: Page)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO page(canonical, title, navigationOrder, content)
      VALUES (${page.canonical}, ${page.title}, ${page.order}, ${page.content})
    """
      .execute()
      .apply()
  }

  def update(page: Page): Validation[Page] = withCommandTransaction { implicit session =>
    updatePage(page)
    Valid(page)
  }

  private def updatePage(page: Page)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE page
      SET title = ${page.title},
          navigationorder = ${page.order},
          content = ${page.content}
      WHERE canonical = ${page.canonical}
    """
      .execute()
      .apply()
  }
}