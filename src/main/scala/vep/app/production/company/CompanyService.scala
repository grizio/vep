package vep.app.production.company

import scalikejdbc._
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class CompanyService() extends DatabaseContainer {
  import CompanyService._

  def findAll(): Seq[Company] = withQueryConnection { implicit session =>
    findAllCompanies()
  }

  private def findAllCompanies()(implicit session: DBSession): Seq[Company] = {
    sql"""
      SELECT * FROM company
    """
      .map(toCompany)
      .list()
      .apply()
  }

  def find(id: String): Option[Company] = withQueryConnection { implicit session =>
    findCompany(id)
  }

  private def findCompany(id: String)(implicit session: DBSession): Option[Company] = {
    sql"""
      SELECT * FROM company
      WHERE id = ${id}
    """
      .map(toCompany)
      .single()
      .apply()
  }

  def create(company: Company): Validation[Company] = withCommandTransaction { implicit session =>
    insertCompany(company)
    Valid(company)
  }

  private def insertCompany(company: Company)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO company(id, name, address, isVep, content)
      VALUES (${company.id}, ${company.name}, ${company.address}, ${company.isVep}, ${company.content})
    """
      .execute()
      .apply()
  }

  def update(company: Company): Validation[Company] = withCommandTransaction { implicit session =>
    updateCompany(company)
    Valid(company)
  }

  private def updateCompany(company: Company)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE company
      SET name = ${company.name},
          address = ${company.address},
          isVep = ${company.isVep},
          content = ${company.content}
      WHERE id = ${company.id}
    """
      .execute()
      .apply()
  }

  def delete(companyId: String): Validation[Unit] = withCommandTransaction { implicit session =>
    deleteCompany(companyId)
    Valid()
  }

  private def deleteCompany(companyId: String)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM company WHERE id = ${companyId}
    """
      .execute()
      .apply()
  }
}

object CompanyService {
  def toCompany(resultSet: WrappedResultSet): Company = {
    new Company(
      id = resultSet.string("id"),
      name = resultSet.string("name"),
      address = resultSet.string("address"),
      isVep = resultSet.boolean("isVep"),
      content = resultSet.string("content"),
    )
  }
}