package vep.model.cms

import vep.model.JsonImplicits
import vep.model.common.{ErrorCodes, VerifiableMultiple}
import vep.utils.StringUtils

case class Page(id: Long, canonical: String, menu: Option[Int], title: String, content: String)

case class PageFormBody(menu: Option[Int], title: String, content: String)
case class PageForm(canonical: String, menu: Option[Int], title: String, content: String) extends VerifiableMultiple {
  override def verify: Boolean = {
    if (StringUtils.isBlank(canonical)) {
      addError("canonical", ErrorCodes.emptyField)
    } else if (StringUtils.isNotCanonical(canonical)) {
      addError("canonical", ErrorCodes.invalidCanonical)
    }
    if (StringUtils.isBlank(title)) {
      addError("title", ErrorCodes.emptyField)
    }
    if (StringUtils.isBlank(content)) {
      addError("content", ErrorCodes.emptyField)
    }
    if (menu.exists(m => m <= 0)) {
      addError("menu", ErrorCodes.negativeOrNull)
    }

    hasNotErrors
  }
}

object PageForm {
  def fromPageFormBody(canonical: String, pageFormBody: PageFormBody) = PageForm(canonical, pageFormBody.menu, pageFormBody.title, pageFormBody.content)
}

object PageImplicits extends JsonImplicits {
  implicit val impPage = jsonFormat(Page.apply, "id", "canonical", "menu", "title", "content")
  implicit val impPageFormBody = jsonFormat(PageFormBody.apply, "menu", "title", "content")
}