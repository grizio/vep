package vep.test.controller.show

import anorm.SqlParser._
import anorm._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultErrors, ResultSuccess}
import vep.model.show.{Show, ShowForm}
import vep.service.AnormImplicits
import vep.service.show.ShowParsers
import vep.test.controller.VepControllersDBInMemoryComponent
import vep.utils.DB

@RunWith(classOf[JUnitRunner])
class ShowControllerUpdateSpecification extends Specification with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB("user/users-with-roles", "show/show-default")
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of ShowController" >> {
    "update should" >> {
      lazy val validShowForm = ShowForm("existing-show", "Updated show", "updated author", "updated director", "existing-company-2", None, None)
      lazy val undefinedShowForm = validShowForm.copy(canonical = "undefined-show")
      lazy val invalidShowForm = ShowForm("existing-show", "", "", "", "", Some(-1), None)

      "updates an existing entry and return a success on valid entity" >> {
        prepare()
        val result = showController.update(validShowForm)
        val showOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM shows WHERE canonical = {canonical}")
            .on("canonical" -> validShowForm.canonical)
            .as(ShowParsers.show.singleOpt)
        }

        (showOpt must beSome[Show]) and
          (result must beAnInstanceOf[Right[_, ResultSuccess]])
      }

      "do not create a new entry and return error on invalid entity" >> {
        prepare()
        val result = showController.update(invalidShowForm)
        val showOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM shows WHERE canonical = {canonical}")
            .on("canonical" -> invalidShowForm.canonical)
            .as(ShowParsers.show.singleOpt)
        }

        (showOpt must beSome.which { s => s.title != invalidShowForm.title }) and
          (result must beAnInstanceOf[Left[ResultErrors, _]])
      }

      "contains same values as given one" >> {
        prepare()
        showController.update(validShowForm)
        val showOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM shows WHERE canonical = {canonical}")
            .on("canonical" -> validShowForm.canonical)
            .as(ShowParsers.show.singleOpt)
        }
        val companyCanonical = showOpt map { s =>
          DB.withConnection { implicit c =>
            SQL("SELECT canonical FROM company WHERE id = {id}")
              .on("id" -> s.company)
              .as(scalar[String].single)
          }
        }

        showOpt must beSome.which { show =>
          show.canonical == validShowForm.canonical &&
            show.title == validShowForm.title &&
            show.author == validShowForm.author &&
            show.director == validShowForm.director &&
            (companyCanonical forall {
              _ == validShowForm.company
            }) &&
            show.duration == validShowForm.duration &&
            show.content == validShowForm.content
        }
      }

      "indicates an error about a field value" >> {
        "when empty canonical" >> {
          prepare()
          val result = showController.update(validShowForm.copy(canonical = ""))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.emptyField))
        }

        "when invalid canonical" >> {
          prepare()
          val result = showController.update(validShowForm.copy(canonical = "It's bad!"))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.invalidCanonical))
        }

        "when big canonical" >> {
          prepare()
          val result = showController.update(validShowForm.copy(canonical = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.bigString))
        }

        "when empty title" >> {
          prepare()
          val result = showController.update(validShowForm.copy(title = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("title")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("title").get must contain(ErrorCodes.emptyField))
        }

        "when big title" >> {
          prepare()
          val result = showController.update(validShowForm.copy(title = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("title")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("title").get must contain(ErrorCodes.bigString))
        }

        "when empty author" >> {
          prepare()
          val result = showController.update(validShowForm.copy(author = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("author")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("author").get must contain(ErrorCodes.emptyField))
        }

        "when big author" >> {
          prepare()
          val result = showController.update(validShowForm.copy(author = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("author")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("author").get must contain(ErrorCodes.bigString))
        }

        "when empty director" >> {
          prepare()
          val result = showController.update(validShowForm.copy(director = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("director")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("director").get must contain(ErrorCodes.emptyField))
        }

        "when big director" >> {
          prepare()
          val result = showController.update(validShowForm.copy(director = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("director")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("director").get must contain(ErrorCodes.bigString))
        }

        "when empty company" >> {
          prepare()
          val result = showController.update(validShowForm.copy(company = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("company")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("company").get must contain(ErrorCodes.emptyField))
        }

        "when negative duration" >> {
          prepare()
          val result = showController.update(validShowForm.copy(duration = Some(-1)))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("duration")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("duration").get must contain(ErrorCodes.negative))
        }
      }

      "indicates an error about a database constraint" >> {
        "when invalid company" >> {
          prepare()
          val result = showController.update(validShowForm.copy(company = "unknown-company"))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("company")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("company").get must contain(ErrorCodes.undefinedCompany))
        }
      }
    }
  }
}
