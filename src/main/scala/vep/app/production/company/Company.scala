package vep.app.production.company

import scalikejdbc.WrappedResultSet
import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class Company(
  id: String,
  name: String,
  address: String,
  isVep: Boolean,
  content: String
)

object Company {
  import JsonProtocol._

  implicit val companyFormat: RootJsonFormat[Company] = jsonFormat5(Company.apply)

  def apply(resultSet: WrappedResultSet): Company = {
    new Company(
      id = resultSet.string("id"),
      name = resultSet.string("name"),
      address = resultSet.string("address"),
      isVep = resultSet.boolean("isVep"),
      content = resultSet.string("content"),
    )
  }
}

case class CompanyCreation(
  name: String,
  address: String,
  isVep: Boolean,
  content: String
)

object CompanyCreation {
  import JsonProtocol._

  implicit val companyCreationFormat: RootJsonFormat[CompanyCreation] = jsonFormat4(CompanyCreation.apply)
}