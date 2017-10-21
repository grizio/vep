package vep.app.seo.views

import java.util.Scanner

import scala.xml.{Node, NodeSeq, Utility}

trait View {
  def render(title: String)(content: => NodeSeq): String = {
    val html = Utility.trim(renderBody(content)).buildString(stripComments = true)
    val index = readIndex
    index
      .replaceAll("<title>(.*)</title>", s"<title>${title}</title>")
      .replace("<body>", s"<body>${html}")
  }

  private def readIndex: String = {
    val inputStream = getClass.getClassLoader.getResourceAsStream(s"front/index.html")
    if (inputStream != null) {
      val scanner = new Scanner(inputStream).useDelimiter("\\A")
      if (scanner.hasNext) {
        scanner.next
      } else {
        ""
      }
    } else {
      ""
    }
  }

  def renderBody(content: => NodeSeq): Node = {
    <div id="app" class="row no-separator">
      <div class="col-fill">
        <div class="page">
          {content}
        </div>
      </div>
    </div>
  }
}
