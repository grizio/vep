package vep.app.production.company.show

import scalikejdbc._
import vep.Configuration
import vep.app.production.company.Company
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class ShowService(
  val configuration: Configuration
) extends DatabaseContainer {
  def create(show: Show, company: Company): Validation[Show] = withCommandTransaction { implicit session =>
    insertShow(show, company)
    Valid(show)
  }

  private def insertShow(show: Show, company: Company)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO show(id, title, author, director, content, company)
      VALUES (${show.id}, ${show.title}, ${show.author}, ${show.director}, ${show.content}, ${company.id})
    """
      .execute()
      .apply()
  }
}