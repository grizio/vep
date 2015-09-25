package vep.test.router.api.reservation


import spray.json.DefaultJsonProtocol

case class InvalidReservationFormBody(name: String)

object InvalidReservationEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidReservationFormBody = jsonFormat1(InvalidReservationFormBody)
}