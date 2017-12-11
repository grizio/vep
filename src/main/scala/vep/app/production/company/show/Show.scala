package vep.app.production.company.show

import spray.json.RootJsonFormat
import vep.app.production.company.Company
import vep.app.production.company.show.play.PlayView
import vep.framework.utils.JsonProtocol

case class ShowMeta(
  id: String,
  title: String,
  company: String
)

object ShowMeta {
  import JsonProtocol._

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