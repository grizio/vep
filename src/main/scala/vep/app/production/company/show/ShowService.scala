package vep.app.production.company.show

import scalikejdbc._
import vep.app.production.company.show.play.PlayService
import vep.app.production.company.{Company, CompanyService}
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class ShowService(
  playService: PlayService
) extends DatabaseContainer {
  import ShowService._

  def findByCompany(company: Company): Seq[Show] = withQueryConnection { implicit session =>
    findShowsByCompany(company)
  }

  private def findShowsByCompany(company: Company)(implicit session: DBSession): Seq[Show] = {
    sql"""
      SELECT * FROM show
      WHERE company = ${company.id}
    """
      .map(toShow)
      .list()
      .apply()
  }

  def findFromCompany(company: Company, id: String): Option[Show] = withQueryConnection { implicit session =>
    findShowFromCompany(company, id)
  }

  def findFromCompanyWithDependencies(company: Company, id: String): Option[ShowWithDependencies] = withQueryConnection { implicit session =>
    findShowFromCompany(company, id)
      .map { show =>
        ShowWithDependencies(
          show = show,
          company = company,
          plays = playService.findAllFromShow(show).toList
        )
      }
  }

  private def findShowFromCompany(company: Company, id: String)(implicit session: DBSession): Option[Show] = {
    sql"""
      SELECT * FROM show
      WHERE id = ${id}
      AND   company = ${company.id}
    """
      .map(toShow)
      .single()
      .apply()
  }

  def findNext(): Seq[ShowMeta] = withQueryConnection { implicit session =>
    findNextShows()
  }

  private def findNextShows()(implicit session: DBSession): Seq[ShowMeta] = {
    sql"""
      SELECT s.* FROM show s
      JOIN play p ON p.show = s.id
      WHERE p.date > CURRENT_TIMESTAMP
      AND NOT EXISTS (
        SELECT 1 FROM play p2
        WHERE p2.show = p.show
        AND p2.date > p.date
      )
      ORDER BY p.date ASC
    """
      .map(toShowMeta)
      .list()
      .apply()
  }

  def findAllWithDependencies(): Seq[ShowWithDependencies] = withQueryConnection { implicit session =>
    findAllShows().flatMap { show =>
      findCompanyByShow(show).map { company =>
        ShowWithDependencies(
          show = show,
          company = company,
          plays = playService.findAllFromShow(show).toList
        )
      }
    }
  }

  private def findAllShows()(implicit session: DBSession): Seq[Show] = {
    sql"""
      SELECT * FROM show
    """
      .map(toShow)
      .list()
      .apply()
  }

  private def findCompanyByShow(show: Show)(implicit session: DBSession): Option[Company] = {
    sql"""
      SELECT * FROM company c
      WHERE EXISTS (
        SELECT 1 FROM show s
        WHERE s.company = c.id
        AND s.id = ${show.id}
      )
    """
      .map(CompanyService.toCompany)
      .single()
      .apply()
  }

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

  def update(show: Show): Validation[Show] = withCommandTransaction { implicit session =>
    updateShow(show)
    Valid(show)
  }

  private def updateShow(show: Show)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE show
      SET title = ${show.title},
          author = ${show.author},
          director = ${show.director},
          content = ${show.content}
      WHERE id = ${show.id}
    """
      .execute()
      .apply()
  }

  def delete(companyId: String, showId: String): Validation[Unit] = withCommandTransaction { implicit session =>
    deleteShow(companyId, showId)
    Valid()
  }

  private def deleteShow(companyId: String, showId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM show
      WHERE id = ${showId} AND company = ${companyId}
    """
      .execute()
      .apply()
  }
}

object ShowService {
  def toShow(resultSet: WrappedResultSet): Show = {
    new Show(
      id = resultSet.string("id"),
      title = resultSet.string("title"),
      author = resultSet.string("author"),
      director = resultSet.string("director"),
      content = resultSet.string("content"),
    )
  }

  def toShowMeta(resultSet: WrappedResultSet): ShowMeta = {
    new ShowMeta(
      id = resultSet.string("id"),
      title = resultSet.string("title"),
      company = resultSet.string("company")
    )
  }
}