package vep.model.company

import spray.json.{JsArray, JsValue, RootJsonFormat}
import vep.model.JsonImplicits
import vep.model.common.{ErrorCodes, VerifiableMultiple}
import vep.utils.StringUtils

case class Company(id: Int, canonical: String, name: String, address: Option[String], isVep: Boolean, content: Option[String])

case class CompanyFormBody(name: String, address: Option[String], isVep: Boolean, content: Option[String])

case class CompanyForm(canonical: String, name: String, address: Option[String], isVep: Boolean, content: Option[String]) extends VerifiableMultiple {
  override def verify: Boolean = {
    if (StringUtils.isBlank(canonical)) {
      addError("canonical", ErrorCodes.emptyField)
    } else if (StringUtils.isNotCanonical(canonical)) {
      addError("canonical", ErrorCodes.invalidCanonical)
    } else if (canonical.length > 255) {
      addError("canonical", ErrorCodes.bigString)
    }

    if (StringUtils.isBlank(name)) {
      addError("name", ErrorCodes.emptyField)
    } else if (name.length > 255) {
      addError("name", ErrorCodes.bigString)
    }

    if (address.exists(address => address.length > 255)) {
      addError("address", ErrorCodes.bigString)
    }

    hasNotErrors
  }
}

case class CompanySeq(companies: Seq[Company])

object CompanyForm {
  def fromCompanyFormBody(canonical: String, companyFormBody: CompanyFormBody) = CompanyForm(canonical, companyFormBody.name, companyFormBody.address, companyFormBody.isVep, companyFormBody.content)
}

object CompanyImplicits extends JsonImplicits {
  implicit val impCompany: RootJsonFormat[Company] = jsonFormat(Company.apply, "id", "canonical", "name", "address", "isVep", "content")
  implicit val impCompanyFormBody: RootJsonFormat[CompanyFormBody] = jsonFormat(CompanyFormBody.apply, "name", "address", "isVep", "content")

  implicit val impCompanySeq = new RootJsonFormat[CompanySeq] {
    override def read(value: JsValue) = CompanySeq(value.convertTo[Seq[Company]])

    override def write(f: CompanySeq) = JsArray(f.companies map { company => impCompany.write(company) }: _*)
  }
}