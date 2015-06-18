package vep.test.controller

import org.specs2.mutable.Specification
import vep.controller.{PageControllerProductionComponent, UserControllerProductionComponent}
import vep.model.cms.{PageItem, PageForm}
import vep.model.common._
import vep.model.user.{UserForAdmin, User, UserLogin, UserRegistration}
import vep.test.service.inmemory.VepServicesInMemoryComponent

class PageControllerForSpecificationComponent
  extends PageControllerProductionComponent
  with VepServicesInMemoryComponent {
  override def overrideServices: Boolean = false
}

class PageControllerSpecification extends Specification {
  "Specifications of PagerController" >> {
    "create should" >> {
      "create a valid entity" >> {
        val pcComp = new PageControllerForSpecificationComponent
        val pageForm = PageForm("this-is-a-test", Some(3), "This is a test", "This content is only a test")

        val result = pcComp.pageController.create(pageForm)

        result must beAnInstanceOf[Right[_, ResultSuccess]]
      }

      "refuse an entity with invalid fields" >> {
        val pcComp = new PageControllerForSpecificationComponent
        val pageForm = PageForm("", None, "", "")

        val result = pcComp.pageController.create(pageForm)

        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "indicates an error about a field value" >> {
        "when empty canonical" >> {
          val pcComp = new PageControllerForSpecificationComponent
          val pageForm = PageForm("", None, "", "")

          val result = pcComp.pageController.create(pageForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.emptyField))
        }

        "when invalid canonical" >> {
          val pcComp = new PageControllerForSpecificationComponent
          val pageForm = PageForm("a b", None, "", "")

          val result = pcComp.pageController.create(pageForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.invalidCanonical))
        }

        "when empty title" >> {
          val pcComp = new PageControllerForSpecificationComponent
          val pageForm = PageForm("", None, "", "")

          val result = pcComp.pageController.create(pageForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("title")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("title").get must contain(ErrorCodes.emptyField))
        }

        "when empty content" >> {
          val pcComp = new PageControllerForSpecificationComponent
          val pageForm = PageForm("", None, "", "")

          val result = pcComp.pageController.create(pageForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("content")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("content").get must contain(ErrorCodes.emptyField))
        }

        "when negative menu" >> {
          val pcComp = new PageControllerForSpecificationComponent
          val pageForm = PageForm("", Some(-1), "", "")

          val result = pcComp.pageController.create(pageForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("menu")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("menu").get must contain(ErrorCodes.negativeOrNull))
        }

        "when menu as 0" >> {
          val pcComp = new PageControllerForSpecificationComponent
          val pageForm = PageForm("", Some(0), "", "")

          val result = pcComp.pageController.create(pageForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("menu")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("menu").get must contain(ErrorCodes.negativeOrNull))
        }
      }

      "indicates an error about a database constraint" >> {
        "when used canonical" >> {
          val pcComp = new PageControllerForSpecificationComponent
          val pageForm = PageForm("homepage", None, "sth", "sth")

          val result = pcComp.pageController.create(pageForm)

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.usedCanonical))
        }
      }
    }

    "list should" >> {
      "returns a sequence of Page" >> {
        val pcComp = new PageControllerForSpecificationComponent
        val result = pcComp.pageController.list().entity

        result must beAnInstanceOf[Seq[PageItem]]
      }
    }
  }
}
