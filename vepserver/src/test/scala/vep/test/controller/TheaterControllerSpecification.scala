package vep.test.controller

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.controller.TheaterControllerProductionComponent
import vep.model.common.{ErrorCodes, ResultErrors, ResultSuccess}
import vep.model.theater.TheaterForm
import vep.test.service.inmemory.VepServicesInMemoryComponent

class TheaterControllerForSpecificationComponent
  extends TheaterControllerProductionComponent
  with VepServicesInMemoryComponent {
  override def overrideServices: Boolean = false
}

@RunWith(classOf[JUnitRunner])
class TheaterControllerSpecification extends Specification {
  "Specifications of TheaterController" >> {
    "create should" >> {
      val validTheaterForm = TheaterForm(
        canonical = "my-new-theater",
        name = "My new theater",
        address = "2 road of the fun, Somewhere",
        content = Some("One content"),
        fixed = true,
        plan = Some( """[{"name":"A1","x":0,"y":0,"w":50,"h":50},{"name":"A2","x":50,"y":0,"w":50,"h":50}]"""),
        maxSeats = None
      )
      val invalidTheaterForm = TheaterForm(
        canonical = "",
        name = "",
        address = "",
        content = None,
        fixed = true,
        plan = None,
        maxSeats = None
      )

      "create a valid entity" >> {
        val tcComp = new TheaterControllerForSpecificationComponent
        val result = tcComp.theaterController.create(validTheaterForm)
        result must beAnInstanceOf[Right[_, ResultSuccess]]
      }

      "refuse an entity with invalid fields" >> {
        val tcComp = new TheaterControllerForSpecificationComponent
        val result = tcComp.theaterController.create(invalidTheaterForm)
        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "indicates an error about a field value" >> {
        "when empty canonical" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(canonical = ""))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.emptyField))
        }

        "when invalid canonical" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(canonical = "It's bad!"))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.invalidCanonical))
        }

        "when big canonical" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(canonical = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.bigString))
        }

        "when empty name" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(name = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.emptyField))
        }

        "when big name" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(name = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.bigString))
        }

        "when empty address" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(address = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("address")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("address").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = false and maxSeats is not defined" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(fixed = false, maxSeats = None))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("maxSeats")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("maxSeats").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = false and maxSeats is negative" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(fixed = false, maxSeats = Some(-1)))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("maxSeats")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("maxSeats").get must contain(ErrorCodes.negativeOrNull))
        }

        "when fixed = false and maxSeats is 0" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(fixed = false, maxSeats = Some(0)))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("maxSeats")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("maxSeats").get must contain(ErrorCodes.negativeOrNull))
        }

        "when fixed = true and plan is not defined" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(fixed = true, plan = None))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("plan")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("plan").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = true and plan is empty" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(fixed = true, plan = Some("")))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("plan")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("plan").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = true and plan is invalid json" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(fixed = true, plan = Some("abcd ' , ef")))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("plan")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("plan").get must contain(ErrorCodes.invalidJson))
        }
      }

      "indicates an error about a database constraint" >> {
        "when used canonical" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.create(validTheaterForm.copy(canonical = "my-theater"))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("canonical")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("canonical").get must contain(ErrorCodes.usedCanonical))
        }
      }
    }

    "update should" >> {
      val validTheaterForm = TheaterForm(
        canonical = "my-theater",
        name = "My existing theater",
        address = "2 road of the fun, Somewhere",
        content = Some("One content"),
        fixed = true,
        plan = Some( """[{"name":"A1","x":0,"y":0,"w":50,"h":50},{"name":"A2","x":50,"y":0,"w":50,"h":50}]"""),
        maxSeats = None
      )
      val invalidTheaterForm = TheaterForm(
        canonical = "my-theater",
        name = "",
        address = "",
        content = None,
        fixed = true,
        plan = None,
        maxSeats = None
      )

      "update a valid entity" >> {
        val tcComp = new TheaterControllerForSpecificationComponent
        val result = tcComp.theaterController.update(validTheaterForm)
        result must beAnInstanceOf[Right[_, ResultSuccess]]
      }

      "refuse an entity which does not exist in database" >> {
        val tcComp = new TheaterControllerForSpecificationComponent
        val result = tcComp.theaterController.update(validTheaterForm.copy(canonical = "unknown-theater"))
        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "refuse an entity with invalid fields" >> {
        val tcComp = new TheaterControllerForSpecificationComponent
        val result = tcComp.theaterController.update(invalidTheaterForm)
        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "refuse an entity with invalid fields" >> {
        val tcComp = new TheaterControllerForSpecificationComponent
        val result = tcComp.theaterController.update(invalidTheaterForm)
        result must beAnInstanceOf[Left[ResultErrors, _]]
      }

      "indicates an error about a field value" >> {
        "when empty name" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(name = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.emptyField))
        }

        "when big name" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(name = "a" * 256))

          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("name")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("name").get must contain(ErrorCodes.bigString))
        }

        "when empty address" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(address = ""))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("address")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("address").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = false and maxSeats is not defined" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(fixed = false, maxSeats = None))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("maxSeats")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("maxSeats").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = false and maxSeats is negative" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(fixed = false, maxSeats = Some(-1)))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("maxSeats")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("maxSeats").get must contain(ErrorCodes.negativeOrNull))
        }

        "when fixed = false and maxSeats is 0" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(fixed = false, maxSeats = Some(0)))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("maxSeats")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("maxSeats").get must contain(ErrorCodes.negativeOrNull))
        }

        "when fixed = true and plan is not defined" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(fixed = true, plan = None))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("plan")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("plan").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = true and plan is empty" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(fixed = true, plan = Some("")))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("plan")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("plan").get must contain(ErrorCodes.emptyField))
        }

        "when fixed = true and plan is invalid json" >> {
          val tcComp = new TheaterControllerForSpecificationComponent
          val result = tcComp.theaterController.update(validTheaterForm.copy(fixed = true, plan = Some("abcd ' , ef")))
          (result must beAnInstanceOf[Left[ResultErrors, _]]) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("plan")) and
            (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("plan").get must contain(ErrorCodes.invalidJson))
        }
      }

      "do not update a locked theater" >> {
        val tcComp = new TheaterControllerForSpecificationComponent
        val result = tcComp.theaterController.update(validTheaterForm.copy(canonical = "locked-theater"))
        (result must beAnInstanceOf[Left[ResultErrors, _]]) and
          (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey("_")) and
          (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get("_").get must contain(ErrorCodes.lockedTheater))
      }
    }
  }
}
