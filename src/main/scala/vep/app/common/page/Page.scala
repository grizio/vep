package vep.app.common.page

import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Page(
  canonical: String,
  title: String,
  order: Int,
  content: String
)

object Page {
  import JsonProtocol._

  implicit val pageFormat: RootJsonFormat[Page] = jsonFormat4(Page.apply)

  def apply(resultSet: WrappedResultSet): Page = {
    new Page(
      canonical = resultSet.string("canonical"),
      title = resultSet.string("title"),
      order = resultSet.int("navigationOrder"),
      content = resultSet.string("content"),
    )
  }
}