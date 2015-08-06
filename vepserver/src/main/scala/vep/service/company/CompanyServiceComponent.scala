package vep.service.company

import anorm.SqlParser._
import anorm._
import vep.AnormClient
import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.company.{Company, CompanyForm}
import vep.service.AnormImplicits
import vep.utils.DB

/**
 * Defines services impacting users.
 */
trait CompanyServiceComponent {
  def companyService: CompanyService

  trait CompanyService {
    /**
     * Inserts a company into database
     * @param companyForm The company to insert
     */
    def create(companyForm: CompanyForm): Unit

    /**
     * Updates an existing company from database
     * @param companyForm The company to update
     */
    def update(companyForm: CompanyForm): Unit

    /**
     * Checks if the company with given canonical exists
     * @param canonical The canonical to check
     * @return True if there is a company with given canonical, otherwise false
     */
    def exists(canonical: String): Boolean

    /**
     * Returns the list of all companies
     */
    def findAll(): Seq[Company]

    /**
     * Finds a company by its canonical.
     * @param canonical The company canonical
     * @return The company with this canonical if exists.
     */
    def find(canonical: String): Option[Company]
  }

}

trait CompanyServiceProductionComponent extends CompanyServiceComponent {
  self: AnormClient =>
  override lazy val companyService = new CompanyServiceProduction

  class CompanyServiceProduction extends CompanyService with AnormImplicits {
    override def create(companyForm: CompanyForm): Unit = DB.withTransaction { implicit c =>
      // The use of SELECT FOR UPDATE provides a way to block other transactions
      // and to not throw any exception for because of canonical duplication.
      val nCanonical = SQL("SELECT count(*) FROM company WHERE canonical = {canonical} FOR UPDATE")
        .on("canonical" -> companyForm.canonical)
        .as(scalar[Long].single)

      if (nCanonical > 0) {
        throw new FieldErrorException("canonical", ErrorCodes.usedCanonical, "The canonical is already used.")
      } else {
        SQL("INSERT INTO company(canonical, name, address, isvep, content) VALUES({canonical}, {name}, {address}, {isvep}, {content})")
          .on("canonical" -> companyForm.canonical)
          .on("name" -> companyForm.name)
          .on("address" -> companyForm.address)
          .on("isvep" -> companyForm.isVep)
          .on("content" -> companyForm.content)
          .executeInsert()
      }
    }

    override def update(companyForm: CompanyForm): Unit = DB.withTransaction { implicit c =>
      SQL( """
             | UPDATE company
             | SET name = {name},
             | address = {address},
             | isvep = {isvep},
             | content = {content}
             | WHERE canonical = {canonical}
             | """.stripMargin)
        .on("canonical" -> companyForm.canonical)
        .on("name" -> companyForm.name)
        .on("address" -> companyForm.address)
        .on("isvep" -> companyForm.isVep)
        .on("content" -> companyForm.content)
        .executeUpdate()
    }

    override def exists(canonical: String): Boolean = DB.withConnection { implicit c =>
      val n = SQL("SELECT COUNT(*) FROM company WHERE canonical = {canonical}")
        .on("canonical" -> canonical)
        .as(scalar[Long].single)
      n == 1
    }

    override def findAll(): Seq[Company] = DB.withConnection { implicit c =>
      SQL("SELECT id, canonical, name, address, isvep, content FROM company").as(CompanyParsers.company *)
    }

    override def find(canonical: String): Option[Company] = DB.withConnection { implicit c =>
      SQL("SELECT id, canonical, name, address, isvep, content FROM company WHERE canonical = {canonical}")
        .on("canonical" -> canonical)
        .as(CompanyParsers.company.singleOpt)
    }
  }

}

object CompanyParsers {
  lazy val company =
    int("id") ~
      str("canonical") ~
      str("name") ~
      get[Option[String]]("address") ~
      int("isvep") ~ // not boolean in database
      get[Option[String]]("content") map {
      case id ~ canonical ~ name ~ address ~ isVep ~ content =>
        Company(id, canonical, name, address, isVep == 1, content)
    }
}