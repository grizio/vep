package vep.test.service.inmemory.cms

import vep.exception.FieldErrorException
import vep.model.cms.{PageItem, Page, PageForm}
import vep.model.common.ErrorCodes
import vep.service.cms.PageServiceComponent
import vep.test.service.inmemory.VepServicesInMemoryComponent

trait PageServiceInMemoryComponent extends PageServiceComponent {
  self: VepServicesInMemoryComponent =>
  lazy val pageServicePermanent = new PageServiceInMemory

  override def pageService: PageService = if (overrideServices) new PageServiceInMemory else pageServicePermanent

  class PageServiceInMemory extends PageService {
    private var pages = Seq[Page](
      Page(1, "homepage", Some(1), "Home", "Welcome to my home page"),
      Page(2, "my-first-page", Some(1), "My first page", "This is my first page"),
      Page(3, "my-hidden-page", None, "My hidden page", "This page should not be visible in menu")
    )

    override def create(pageForm: PageForm): Unit = {
      if (pages.exists(p => p.canonical == pageForm.canonical)) {
        throw new FieldErrorException("canonical", ErrorCodes.usedCanonical, "The canonical is already used.")
      } else {
        pages = pages.+:(Page(pages.maxBy(p => p.id).id + 1, pageForm.canonical, pageForm.menu, pageForm.title, pageForm.content))
      }
    }

    override def update(pageForm: PageForm): Unit = {
      pages = pages map { p =>
        if (p.canonical == pageForm.canonical) {
          p.copy(menu = p.menu, content = p.content, title = p.title)
        } else {
          p
        }
      }
    }

    override def findAll(): Seq[Page] = pages map { p => p.copy() }

    override def exists(canonical: String): Boolean = {
      pages exists { p => p.canonical == canonical }
    }
  }

}
