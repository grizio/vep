package vep.test.router.api.session

import spray.json.DefaultJsonProtocol

case class InvalidSessionFormBody(name: String)

object InvalidSessionEntitiesImplicits extends DefaultJsonProtocol {
  implicit val impInvalidSessionFormBody = jsonFormat1(InvalidSessionFormBody)
}