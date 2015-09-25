package vep.model.session

import spray.json.DefaultJsonProtocol._
import vep.model.common.{ErrorItem, ErrorCodes, VerifiableMultipleStructured}
import vep.utils.StringUtils

case class Reservation(id: Int, session: Int, firstName: String, lastName: String, email: String, city: Option[String],
                       comment: Option[String], seats: Option[Int], key: String)

case class ReservationSeat(reservation: Int, seat: String)

case class ReservationPrice(reservation: Int, price: Int, number: Int)

case class ReservationForm(theater: String, session: String, firstName: String, lastName: String, email: String,
                           city: Option[String], comment: Option[String], seats: Option[Int], seatList: Seq[String],
                           prices: Seq[ReservationPriceForm]) extends VerifiableMultipleStructured {
  override protected def doVerify(): Unit = {
    if (StringUtils.isBlank(theater)) {
      addError("theater", ErrorCodes.emptyField)
    }

    if (StringUtils.isBlank(session)) {
      addError("session", ErrorCodes.emptyField)
    }

    if (StringUtils.isBlank(firstName)) {
      addError("firstName", ErrorCodes.emptyField)
    } else if (firstName.length > 250) {
      addError("firstName", ErrorCodes.bigString)
    }

    if (StringUtils.isBlank(lastName)) {
      addError("lastName", ErrorCodes.emptyField)
    } else if (lastName.length > 250) {
      addError("lastName", ErrorCodes.bigString)
    }

    if (StringUtils.isBlank(email)) {
      addError("email", ErrorCodes.emptyField)
    } else if (StringUtils.isNotEmail(email)) {
      addError("email", ErrorCodes.invalidEmail)
    } else if (email.length > 250) {
      addError("email", ErrorCodes.bigString)
    }

    if (city.exists(_.length > 250)) {
      addError("city", ErrorCodes.bigString)
    }

    if (prices.isEmpty) {
      addError("prices", ErrorCodes.emptyField)
    }

    if (seats.exists(_ <= 0)) {
      addError("seats", ErrorCodes.negativeOrNull)
    }

    val numberSeats = math.max(seats.getOrElse(0), seatList.length)
    val numberSeatsPrice = prices.foldLeft(0)((result, price) => result + price.number)
    if (numberSeats != numberSeatsPrice) {
      addError("prices", ErrorCodes.differentSeatPriceNumbers)
    }

    if (seatList.nonEmpty) {
      val seatListErrors = ErrorItem.seqToErrorItemSeq(seatList, ErrorCodes.emptyField)(StringUtils.isBlank)
      if (seatListErrors.isNotEmpty) {
        addErrorItem("seatList", seatListErrors)
      }
    }
  }
}

case class ReservationFormBody(firstName: String, lastName: String, email: String, city: Option[String],
                               comment: Option[String], seats: Option[Int], seatList: Seq[String],
                               prices: Seq[ReservationPriceForm]) {
  def toReservationForm(theater: String, session: String) =
    ReservationForm(theater, session, firstName, lastName, email, city, comment, seats, seatList, prices)
}

case class ReservationPriceForm(price: Int, number: Int)

case class ReservationFormResult(id: Int, key: String)

case class ReservationDetail(id: Int, session: String, firstName: String, lastName: String, email: String,
                             city: Option[String], comment: Option[String], seats: Option[Int], pass: String,
                             seatList: Seq[String], prices: Seq[ReservationPriceDetail])

case class ReservationPriceDetail(price: Int, number: Int, value: Int)

