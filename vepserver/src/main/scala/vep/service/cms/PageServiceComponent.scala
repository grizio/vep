package vep.service.cms

import anorm.SqlParser._
import anorm._
import spray.http.DateTime
import spray.routing.authentication.UserPass
import vep.AnormClient
import vep.exception.FieldErrorException
import vep.model.cms.{PageItem, Page, PageForm}
import vep.model.common.ErrorCodes
import vep.model.user.{UserForAdmin, User, UserLogin, UserRegistration}
import vep.service.AnormImplicits
import vep.utils.{StringUtils, DB}

/**
 * Defines services impacting presentation pages.
 */
trait PageServiceComponent {
  def pageService: PageService

  trait PageService {
    /**
     * Inserts a page into database
     * @param pageForm The page to insert
     */
    def create(pageForm: PageForm): Unit

    /**
     * Updates an existing page from database.
     * @param pageForm The page to update
     */
    def update(pageForm: PageForm): Unit

    /**
     * Returns the whole list of pages
     */
    def findAll(): Seq[PageItem]

    /**
     * Checks if the given page canonical exists in database.
     * @param canonical The canonical to check
     * @return True if there is a page with given canonical, otherwise false.
     */
    def exists(canonical: String): Boolean
  }

}

trait PageServiceProductionComponent extends PageServiceComponent {
  self: AnormClient =>
  override lazy val pageService = new PageServiceProduction

  class PageServiceProduction extends PageService with AnormImplicits {
    lazy val pageParser =
      int("id") ~
        str("canonical") ~
        get[Option[Int]]("menu") ~
        str("title") ~
        str("content") map {
        case id ~ canonical ~ menu ~ title ~ content =>
          Page(id, canonical, menu, title, content)
      }

    lazy val pageItemParser =
        str("canonical") ~
        get[Option[Int]]("menu") ~
        str("title") map {
        case canonical ~ menu ~ title =>
          PageItem(canonical, menu, title)
      }

    override def create(pageForm: PageForm): Unit = DB.withTransaction { implicit c =>
      // The use of SELECT FOR UPDATE provides a way to block other transactions
      // and to not throw any exception for because of email duplication.
      val nCanonical = SQL("SELECT count(*) FROM page WHERE canonical = {canonical} FOR UPDATE")
        .on("canonical" -> pageForm.canonical)
        .as(scalar[Int].single)

      if (nCanonical > 0) {
        throw new FieldErrorException("canonical", ErrorCodes.usedCanonical, "The canonical is already used.")
      } else {
        SQL("INSERT INTO page(canonical, menu, title, content) VALUES ({canonical}, {menu}, {title}, {content})")
          .on("canonical" -> pageForm.canonical)
          .on("menu" -> pageForm.menu)
          .on("title" -> pageForm.title)
          .on("content" -> pageForm.content)
          .executeInsert()
      }
    }

    override def update(pageForm: PageForm): Unit = DB.withTransaction { implicit c =>
        SQL("UPDATE page SET menu = {menu}, title = {title}, content = {content} WHERE canonical = {canonical}")
          .on("canonical" -> pageForm.canonical)
          .on("menu" -> pageForm.menu)
          .on("title" -> pageForm.title)
          .on("content" -> pageForm.content)
          .executeUpdate()
    }

    override def findAll(): Seq[PageItem] = DB.withConnection { implicit c =>
      SQL("SELECT canonical, menu, title FROM page p").as(pageItemParser *)
    }

    override def exists(canonical: String): Boolean = DB.withConnection { implicit c =>
      val n = SQL("SELECT COUNT(*) FROM page WHERE canonical = {canonical}")
        .on("canonical" -> canonical)
        .as(scalar[Int].single)
      n == 0
    }
  }
}
