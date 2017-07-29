package vep.app.common.page

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class PageService(
  val configuration: Configuration
) extends DatabaseContainer {
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
}