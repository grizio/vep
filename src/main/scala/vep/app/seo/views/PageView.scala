package vep.app.seo.views

import vep.app.common.page.Page

object PageView extends View {
  def view(page: Page): String = render(page.title) {
    <header>{page.title}</header>
    <section>
      <div class="">
        <div>
          <div class="rich-content">
            {RichContent.format(page.content)}
          </div>
        </div>
      </div>
    </section>
  }
}
