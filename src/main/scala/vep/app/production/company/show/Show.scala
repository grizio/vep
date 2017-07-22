package vep.app.production.company.show

import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Show(
  id: String,
  title: String,
  author: String,
  director: String,
  content: String
)

object Show {
  import JsonProtocol._

  implicit val showFormat: RootJsonFormat[Show] = jsonFormat5(Show.apply)

  def apply(resultSet: WrappedResultSet): Show = {
    new Show(
      id = resultSet.string("id"),
      title = resultSet.string("title"),
      author = resultSet.string("author"),
      director = resultSet.string("director"),
      content = resultSet.string("content"),
    )
  }
}

case class ShowCreation(
  title: String,
  author: String,
  director: String,
  content: String
)

object ShowCreation {
  import JsonProtocol._

  implicit val showCreationFormat: RootJsonFormat[ShowCreation] = jsonFormat4(ShowCreation.apply)
}