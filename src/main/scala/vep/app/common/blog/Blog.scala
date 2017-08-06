package vep.app.common.blog

import org.joda.time.DateTime
import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Blog(
  id: String,
  title: String,
  date: DateTime,
  content: String
)

object Blog {
  import JsonProtocol._

  implicit val blogFormat: RootJsonFormat[Blog] = jsonFormat4(Blog.apply)

  def apply(resultSet: WrappedResultSet): Blog = {
    new Blog(
      id = resultSet.string("id"),
      title = resultSet.string("title"),
      date = resultSet.jodaDateTime("date"),
      content = resultSet.string("content"),
    )
  }
}

case class BlogCreation(
  title: String,
  content: String
)

object BlogCreation {
  import JsonProtocol._

  implicit val blogCreationFormat: RootJsonFormat[BlogCreation] = jsonFormat2(BlogCreation.apply)
}

case class BlogUpdate(
  id: String,
  title: String,
  content: String
)

object BlogUpdate {
  import JsonProtocol._

  implicit val blogUpdateFormat: RootJsonFormat[BlogUpdate] = jsonFormat3(BlogUpdate.apply)
}