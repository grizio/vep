package vep.service.show

import anorm.SqlParser._
import anorm._
import vep.AnormClient
import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.show.{Show, ShowForm}
import vep.service.AnormImplicits
import vep.service.company.CompanyServiceComponent
import vep.utils.DB

/**
 * Defines services impacting shows.
 */
trait ShowServiceComponent {
  def showService: ShowService

  trait ShowService {
    /**
     * Inserts a show into database
     * @param showForm The show to insert
     */
    def create(showForm: ShowForm): Unit

    /**
     * Checks if a show with given canonical exists.
     * @param canonical The canonical to check.
     * @return True if a show exists, otherwise false.
     */
    def exists(canonical: String): Boolean

    /**
     * Updates an existing show from database.
     * @param showForm The show to update
     */
    def update(showForm: ShowForm): Unit
  }

}

trait ShowServiceProductionComponent extends ShowServiceComponent {
  self: AnormClient with CompanyServiceComponent =>
  override lazy val showService = new ShowServiceProduction

  class ShowServiceProduction extends ShowService with AnormImplicits {
    override def create(showForm: ShowForm): Unit = DB.withTransaction { implicit c =>
      if (exists(showForm.canonical)) {
        throw new FieldErrorException("canonical", ErrorCodes.usedCanonical, "The canonical is already used.")
      } else {
        companyService.find(showForm.company) match {
          case Some(company) =>
            SQL("INSERT INTO shows(canonical, title, author, director, company, duration, content) VALUES({canonical}, {title}, {author}, {director}, {company}, {duration}, {content})")
              .on("canonical" -> showForm.canonical)
              .on("title" -> showForm.title)
              .on("author" -> showForm.author)
              .on("director" -> showForm.director)
              .on("company" -> company.id)
              .on("duration" -> showForm.duration)
              .on("content" -> showForm.content)
              .executeInsert()
          case None => throw new FieldErrorException("company", ErrorCodes.undefinedCompany, "The company does not exists.")
        }
      }
    }

    override def exists(canonical: String): Boolean = DB.withConnection { implicit c =>
      val n = SQL("SELECT count(*) FROM shows WHERE canonical = {canonical}")
        .on("canonical" -> canonical)
        .as(scalar[Long].single)
      n == 1
    }

    override def update(showForm: ShowForm): Unit = DB.withConnection { implicit c =>
      companyService.find(showForm.company) match {
        case Some(company) =>
          SQL(
            """
              | UPDATE shows SET
              | title = {title},
              | author = {author},
              | director = {director},
              | company = {company},
              | duration = {duration},
              | content = {content}
              | WHERE canonical = {canonical}
            """.stripMargin)
            .on("title" -> showForm.title)
            .on("author" -> showForm.author)
            .on("director" -> showForm.director)
            .on("company" -> company.id)
            .on("duration" -> showForm.duration)
            .on("content" -> showForm.content)
            .on("canonical" -> showForm.canonical)
            .execute()
        case None => throw new FieldErrorException("company", ErrorCodes.undefinedCompany, "The company does not exists.")
      }
    }
  }

}

object ShowParsers {
  lazy val show =
    int("id") ~
      str("canonical") ~
      str("title") ~
      str("author") ~
      str("director") ~
      int("company") ~
      get[Option[Int]]("duration") ~
      get[Option[String]]("content") map {
      case id ~ canonical ~ title ~ author ~ director ~ company ~ duration ~ content =>
        Show(id, canonical, title, author, director, company, duration, content)
    }
}