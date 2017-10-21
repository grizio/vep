package vep.app.seo.views.render

import vep.app.common.page.Page

trait PageRender extends Render {
  def renderPage(page: Page): String = render(page.title) {
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
