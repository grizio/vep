package vep.app.production.company.show

import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.app.production.company.Company
import vep.app.production.company.show.play.{Play, PlayView}
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

case class ShowMeta(
  id: String,
  title: String,
  company: String
)

object ShowMeta {
  import JsonProtocol._

  def apply(resultSet: WrappedResultSet): ShowMeta = {
    new ShowMeta(
      id = resultSet.string("id"),
      title = resultSet.string("title"),
      company = resultSet.string("company")
    )
  }

  implicit val showMetaFormat: RootJsonFormat[ShowMeta] = jsonFormat3(ShowMeta.apply)
}

case class ShowWithDependencies(
  show: Show,
  company: Company,
  plays: Seq[PlayView]
)

object ShowWithDependencies {
  import JsonProtocol._

  implicit val showWithDependenciesFormat: RootJsonFormat[ShowWithDependencies] = jsonFormat3(ShowWithDependencies.apply)
}