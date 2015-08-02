package vep.test.controller

import anorm._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultErrors, ResultSuccess}
import vep.model.company.{Company, CompanyForm}
import vep.service.AnormImplicits
import vep.service.company.CompanyParsers
import vep.utils.DB

@RunWith(classOf[JUnitRunner])
class CompanyControllerSpecification extends Specification with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare: Unit = {
    prepareDB("user/users-with-roles", "company/company-default")
  }

  // Usage of sequential because database state is reset between each state.
  sequential ^ "Specifications of CompanyController" >> {
    "create should" >> {
      lazy val validCompanyForm = CompanyForm("new-company", "New company", Some("Paris, France"), isVep = true, Some("This is my test."))
      lazy val existingCompanyForm = validCompanyForm.copy(canonical = "existing-company")
      lazy val invalidCompanyForm = CompanyForm("invalid-canonical", "", Some("a" * 256), isVep = false, None)

      "create a new entry and return a success on valid entity" >> {
        prepare
        val result = companyController.create(validCompanyForm)
        val companyOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM company WHERE canonical = {canonical}")
            .on("canonical" -> validCompanyForm.canonical)
            .as(CompanyParsers.company.singleOpt)
        }

        (companyOpt must beSome[Company]) and
          (result must beAnInstanceOf[Right[_, ResultSuccess]])
      }

      "do not create a new entry and return error on invalid entity" >> {
        prepare
        val result = companyController.create(invalidCompanyForm)
        val companyOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM company WHERE canonical = {canonical}")
            .on("canonical" -> invalidCompanyForm.canonical)
            .as(CompanyParsers.company.singleOpt)
        }

        (companyOpt must beNone) and
          (result must beAnInstanceOf[Left[ResultErrors, _]])
      }

      "contains same values as given one" >> {
        prepare
        companyController.create(validCompanyForm)
        val companyOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM company WHERE canonical = {canonical}")
            .on("canonical" -> validCompanyForm.canonical)
            .as(CompanyParsers.company.singleOpt)
        }

        companyOpt must beSome.which { company =>
          company.canonical == validCompanyForm.canonical &&
            company.name == validCompanyForm.name &&
            company.address == validCompanyForm.address &&
            company.isVep == validCompanyForm.isVep &&
            company.content == validCompanyForm.content
        }
      }

      "indicates an error about a field value" >> {
        "when empty canonical" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(canonical = ""))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.emptyField))
        }

        "when invalid canonical" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(canonical = "It's bad!"))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.invalidCanonical))
        }

        "when big canonical" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(canonical = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.bigString))
        }

        "when empty name" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(name = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.emptyField))
        }

        "when big name" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(name = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.bigString))
        }

        "when big address" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(address = Some("a" * 256)))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("address")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("address").get must contain(ErrorCodes.bigString))
        }
      }

      "indicates an error about a database constraint" >> {
        "when used canonical" >> {
          prepare
          val result = companyController.create(existingCompanyForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.usedCanonical))
        }
      }
    }

    "update should" >> {
      lazy val validCompanyForm = CompanyForm("existing-company", "Updated company", Some("Berlin, Deutschland"), isVep = true, Some("My updated content"))
      lazy val notExistingCompanyForm = CompanyForm("new-company", "New company", Some("Paris, France"), isVep = true, Some("This is my test."))
      lazy val invalidCompanyForm = CompanyForm("existing-company", "", Some("a" * 256), isVep = false, None)

      "update an existing entry and return a success on valid entity" >> {
        prepare
        val result = companyController.update(validCompanyForm)
        val companyOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM company WHERE canonical = {canonical}")
            .on("canonical" -> validCompanyForm.canonical)
            .as(CompanyParsers.company.singleOpt)
        }

        (companyOpt must beSome[Company]) and
          (result must beAnInstanceOf[Right[_, ResultSuccess]])
      }

      "refuse an entity which does not exist in database" >> {
        prepare
        val result = companyController.update(notExistingCompanyForm)
        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "refuse an invalid entity" >> {
        prepare
        val result = companyController.update(invalidCompanyForm)
        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "contains same values as given one" >> {
        prepare
        companyController.update(validCompanyForm)
        val companyOpt = DB.withConnection { implicit c =>
          SQL("SELECT * FROM company WHERE canonical = {canonical}")
            .on("canonical" -> validCompanyForm.canonical)
            .as(CompanyParsers.company.singleOpt)
        }

        companyOpt must beSome.which { company =>
          company.canonical == validCompanyForm.canonical &&
            company.name == validCompanyForm.name &&
            company.address == validCompanyForm.address &&
            company.isVep == validCompanyForm.isVep &&
            company.content == validCompanyForm.content
        }
      }

      "indicates an error about a field value" >> {
        "when empty canonical" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(canonical = ""))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.emptyField))
        }

        "when invalid canonical" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(canonical = "It's bad!"))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.invalidCanonical))
        }

        "when big canonical" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(canonical = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.bigString))
        }

        "when empty name" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(name = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.emptyField))
        }

        "when big name" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(name = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.bigString))
        }

        "when big address" >> {
          prepare
          val result = companyController.create(validCompanyForm.copy(address = Some("a" * 256)))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("address")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("address").get must contain(ErrorCodes.bigString))
        }
      }
    }

    "list should" >> {
      "Return the whole list of companies" >> {
        prepare
        val result = companyController.list()
        result.entity.length === 2
      }
    }
  }
}
