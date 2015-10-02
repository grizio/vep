package vep.model.cms

import vep.model.JsonImplicits
import vep.model.common.{ErrorCodes, VerifiableMultiple}
import vep.utils.StringUtils

case class Contact(name: String, email: String, title: String, content: String) extends VerifiableMultiple {
  override protected def doVerify(): Unit = {
    if (StringUtils.isBlank(name)) {
      addError("name", ErrorCodes.emptyField)
    }

    if (StringUtils.isBlank(email)) {
      addError("email", ErrorCodes.emptyField)
    } else if (StringUtils.isNotEmail(email)) {
      addError("email", ErrorCodes.invalidEmail)
    }

    if (StringUtils.isBlank(title)) {
      addError("title", ErrorCodes.emptyField)
    }

    if (StringUtils.isBlank(content)) {
      addError("content", ErrorCodes.emptyField)
    }
  }
}

object ContactImplicit extends JsonImplicits {
  implicit val impContact = jsonFormat(Contact.apply, "name", "email", "title", "content")
}