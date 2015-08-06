package vep.test.router.api.show

import spray.json.DefaultJsonProtocol

case class InvalidShowFormBody(name: String)

object InvalidShowEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidShowFormBody = jsonFormat1(InvalidShowFormBody)
}