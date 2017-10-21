package vep.app.seo.views.render

import scala.xml.{Node, NodeSeq, Text}

object Cards {
  def renderCardCollection(columns: Int, children: Seq[Node]): NodeSeq = {
    if (children.nonEmpty) {
      val rows = Math.ceil(children.length.toDouble / columns.toDouble).toInt
      <div>
        {Seq.tabulate(rows) {row => renderRow(columns, children, row)}}
      </div>
    } else {
      Seq.empty
    }
  }

  def renderRow(columns: Int, children: Seq[Node], row: Int): NodeSeq = {
    if ((row + 1) * columns > children.length) {
      val remainingColumns = (row + 1) * columns - children.length
      <div class="row">
        {children.drop(row * columns).map(renderColumn)}
        {Seq.fill(remainingColumns)(renderColumn(Text("")))}
      </div>
    } else {
      <div class="row">
        {children.slice(row * columns, row * columns + row * columns + columns).map(renderColumn)}
      </div>
    }
  }

  def renderColumn(card: Node): Node = {
    <div class="col-1">{card}</div>
  }

  def renderCard(title: String, children: Seq[Node]): Node = {
    <div class="card">
      <div>{renderText(title, children)}</div>
    </div>
  }

  def renderText(title: String, children: Seq[Node]): Node = {
    <div class="card-text">
      <div class="card-title">{title}</div>
      {children}
    </div>
  }
}
