package vep.model.show

import vep.model.common.{ErrorCodes, VerifiableMultiple}
import vep.utils.StringUtils

case class Show(id: Int, canonical: String, title: String, author: String, director: Option[String], company: Int, duration: Option[Int], content: Option[String])

case class ShowForm(canonical: String, title: String, author: String, director: Option[String], company: String, duration: Option[Int], content: Option[String]) extends VerifiableMultiple {
  override def verify: Boolean = {
    if (StringUtils.isEmpty(canonical)) {
      addError("canonical", ErrorCodes.emptyField)
    } else if (StringUtils.isNotCanonical(canonical)) {
      addError("canonical", ErrorCodes.invalidCanonical)
    } else if (canonical.length > 250) {
      addError("canonical", ErrorCodes.bigString)
    }

    if (StringUtils.isEmpty(title)) {
      addError("title", ErrorCodes.emptyField)
    } else if (title.length > 250) {
      addError("title", ErrorCodes.bigString)
    }

    if (StringUtils.isEmpty(author)) {
      addError("author", ErrorCodes.emptyField)
    } else if (author.length > 250) {
      addError("author", ErrorCodes.bigString)
    }

    if (director.exists(_.length > 250)) {
      addError("director", ErrorCodes.bigString)
    }

    if (StringUtils.isEmpty(company)) {
      addError("company", ErrorCodes.emptyField)
    }

    if (duration exists { d => d < 0 }) {
      addError("duration", ErrorCodes.negative)
    }

    hasNotErrors
  }
}

case class ShowFormBody(title: String, author: String, director: Option[String], company: String, duration: Option[Int], content: Option[String]) {
  def toShowForm(canonical: String) = ShowForm(canonical, title, author, director, company, duration, content)
}

// We do not use ShowForm because model of one could change instead of the other
case class ShowDetail(canonical: String, title: String, author: String, director: Option[String], company: String, duration: Option[Int], content: Option[String])
