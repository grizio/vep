package vep.app.user.profile

import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class AccountDeletion(
  email: String,
  password: String
)

object AccountDeletion {
  import JsonProtocol._

  implicit val accountDeletionFormat: RootJsonFormat[AccountDeletion] = jsonFormat2(AccountDeletion.apply)
}