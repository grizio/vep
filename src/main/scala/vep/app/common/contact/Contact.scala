package vep.app.common.contact

import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Contact(
  name: String,
  email: String,
  title: String,
  content: String
)

object Contact {
  import JsonProtocol._

  implicit val contactFormat: RootJsonFormat[Contact] = jsonFormat4(Contact.apply)
}