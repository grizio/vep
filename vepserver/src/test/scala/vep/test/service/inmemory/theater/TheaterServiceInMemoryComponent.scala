package vep.test.service.inmemory.theater

import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.theater.{Theater, TheaterForm}
import vep.service.theater.TheaterServiceComponent
import vep.test.service.inmemory.VepServicesInMemoryComponent

trait TheaterServiceInMemoryComponent extends TheaterServiceComponent {
  self: VepServicesInMemoryComponent =>
  lazy val theaterServicePermanent = new TheaterServiceInMemory

  override def theaterService: TheaterService = if (overrideServices) new TheaterServiceInMemory else theaterServicePermanent

  class TheaterServiceInMemory extends TheaterService {
    private var theaters = Seq[Theater](
      Theater(1, "my-theater", "My Theater", "1 road of the glory", None, fixed = true, Some( """[{"name":"A1","x":0,"y":0,"w":50,"h":50}]"""), None),
      Theater(2, "my-theater-2", "My Theater", "1 road of the glory", None, fixed = false, None, Some(1))
    )

    override def create(theaterForm: TheaterForm): Unit = {
      if (theaters.exists(t => t.canonical == theaterForm.canonical)) {
        throw new FieldErrorException("canonical", ErrorCodes.usedCanonical, "The canonical is already used.")
      } else {
        theaters = theaters.+:(Theater(
          theaters.maxBy(t => t.id).id + 1,
          theaterForm.canonical,
          theaterForm.name,
          theaterForm.address,
          theaterForm.content,
          theaterForm.fixed,
          theaterForm.plan,
          theaterForm.maxSeats
        ))
      }
    }
  }

}
