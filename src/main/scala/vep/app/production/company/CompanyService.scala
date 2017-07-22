package vep.app.production.company

import scalikejdbc._
import vep.Configuration
import vep.app.production.theater.{Seat, Theater}
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class CompanyService(
  val configuration: Configuration
) extends DatabaseContainer {
  def findAll(): Seq[Company] = withQueryConnection { implicit session =>
    findAllCompanies()
  }

  private def findAllCompanies()(implicit session: DBSession): Seq[Company] = {
    sql"""
      SELECT * FROM company
    """
      .map(Company.apply)
      .list()
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
}