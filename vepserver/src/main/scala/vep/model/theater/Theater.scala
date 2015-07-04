package vep.model.theater

import spray.json.{JsArray, JsValue, RootJsonFormat}
import vep.model.JsonImplicits
import vep.model.common.{ErrorCodes, VerifiableMultiple}
import vep.utils.StringUtils

case class Theater(id: Long, canonical: String, name: String, address: String, content: Option[String], fixed: Boolean, plan: Option[String], maxSeats: Option[Int])

case class TheaterFormBody(name: String, address: String, content: Option[String], fixed: Boolean, plan: Option[String], maxSeats: Option[Int])

case class TheaterForm(canonical: String, name: String, address: String, content: Option[String], fixed: Boolean, plan: Option[String], maxSeats: Option[Int]) extends VerifiableMultiple {
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
    if (StringUtils.isBlank(address)) {
      addError("address", ErrorCodes.emptyField)
    }
    if (fixed) {
      if (!plan.exists(StringUtils.isNotEmpty)) {
        addError("plan", ErrorCodes.emptyField)
      } else if (!plan.exists(StringUtils.isJson)) {
        addError("plan", ErrorCodes.invalidJson)
      }
    } else {
      if (maxSeats.isEmpty) {
        addError("maxSeats", ErrorCodes.emptyField)
      } else if (maxSeats.get <= 0) {
        addError("maxSeats", ErrorCodes.negativeOrNull)
      }
    }

    hasNotErrors
  }
}

case class TheaterSeq(theaters: Seq[Theater])

object TheaterForm {
  def fromTheaterFormBody(canonical: String, theaterFormBody: TheaterFormBody) = TheaterForm(canonical, theaterFormBody.name, theaterFormBody.address, theaterFormBody.content, theaterFormBody.fixed, theaterFormBody.plan, theaterFormBody.maxSeats)
}

object TheaterImplicits extends JsonImplicits {
  implicit val impTheater: RootJsonFormat[Theater] = jsonFormat(Theater.apply, "id", "canonical", "name", "address", "content", "fixed", "plan", "maxSeats")
  implicit val impTheaterFormBody: RootJsonFormat[TheaterFormBody] = jsonFormat(TheaterFormBody.apply, "name", "address", "content", "fixed", "plan", "maxSeats")

  implicit val impTheaterSeq = new RootJsonFormat[TheaterSeq] {
    override def read(value: JsValue) = TheaterSeq(value.convertTo[Seq[Theater]])

    override def write(f: TheaterSeq) = JsArray(f.theaters map { theater => impTheater.write(theater) }: _*)
  }
}