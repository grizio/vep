package vep.model.cms

import spray.json.{JsArray, JsValue, RootJsonFormat}
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

case class PageItem(canonical: String, menu: Option[Int], title: String)

case class PageSeq(pages: Seq[Page])

case class PageItemSeq(pages: Seq[PageItem])

object PageForm {
  def fromPageFormBody(canonical: String, pageFormBody: PageFormBody) = PageForm(canonical, pageFormBody.menu, pageFormBody.title, pageFormBody.content)
}

object PageImplicits extends JsonImplicits {
  implicit val impPage: RootJsonFormat[Page] = jsonFormat(Page.apply, "id", "canonical", "menu", "title", "content")
  implicit val impPageFormBody: RootJsonFormat[PageFormBody] = jsonFormat(PageFormBody.apply, "menu", "title", "content")
  implicit val impPageItem: RootJsonFormat[PageItem] = jsonFormat(PageItem.apply, "canonical", "menu", "title")

  implicit val impPageSeq = new RootJsonFormat[PageSeq] {
    def read(value: JsValue) = PageSeq(value.convertTo[Seq[Page]])
    def write(f: PageSeq) = JsArray(f.pages map { page => impPage.write(page) }: _*)
  }

  implicit val impPageItemSeq = new RootJsonFormat[PageItemSeq] {
    def read(value: JsValue) = PageItemSeq(value.convertTo[Seq[PageItem]])
    def write(f: PageItemSeq) = JsArray(f.pages map { page => impPageItem.write(page) }: _*)
  }
}