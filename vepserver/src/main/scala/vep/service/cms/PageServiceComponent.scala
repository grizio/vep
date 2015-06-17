package vep.service.cms

import anorm.SqlParser._
import anorm._
import spray.http.DateTime
import spray.routing.authentication.UserPass
import vep.AnormClient
import vep.exception.FieldErrorException
import vep.model.cms.PageForm
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
     * @param pageForm: PageForm The user to insert
     */
    def create(pageForm: PageForm): Unit
  }

}

trait PageServiceProductionComponent extends PageServiceComponent {
  self: AnormClient =>
  override lazy val pageService = new PageServiceProduction

  class PageServiceProduction extends PageService with AnormImplicits {
    lazy val userParser =
      int("uid") ~
        str("email") ~
        str("password") ~
        str("salt") ~
        str("firstName") ~
        str("lastName") ~
        get[Option[String]]("city") ~
        get[Option[String]]("keyLogin") ~
        get[Option[DateTime]]("expiration") ~
        get[String]("roles") map {
        case uid ~ email ~ password ~ salt ~ firsName ~ lastName ~ city ~ keyLogin ~ expiration ~ roles =>
          User(uid, email, password, salt, firsName, lastName, city, keyLogin, expiration, roles.split(","))
      }

    lazy val userForAdminParser =
      int("uid") ~
        str("email") ~
        str("firstname") ~
        str("lastname") ~
        str("roles") map {
        case uid ~ email ~ firstname ~ lastname ~ roles =>
          UserForAdmin(uid, email, firstname, lastname, roles.split(","))
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
  }

}
