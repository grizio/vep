package vep.model.session

import vep.model.common.{ErrorCodes, ErrorItem, VerifiableMultipleStructured}
import vep.utils.DateUtils

case class SessionForm(theater: String, date: String, reservationEndDate: String, name: String, prices: Seq[SessionPriceForm], shows: Seq[String]) extends VerifiableMultipleStructured {
  lazy val dtDate = DateUtils.toDateTime(date)
  lazy val dtReservationEndDate = DateUtils.toDateTime(reservationEndDate)

  override protected def doVerify(): Unit = {
    if (theater.isEmpty) {
      addError("theater", ErrorCodes.emptyField)
    }

    if (date.isEmpty) {
      addError("date", ErrorCodes.emptyField)
    } else if (DateUtils.isNotIsoDate(date)) {
      addError("date", ErrorCodes.invalidDate)
    } else if (dtDate.isBeforeNow) {
      addError("date", ErrorCodes.dateTooSoon)
    }

    if (reservationEndDate.isEmpty) {
      addError("reservationEndDate", ErrorCodes.emptyField)
    } else if (DateUtils.isNotIsoDate(reservationEndDate)) {
      addError("reservationEndDate", ErrorCodes.invalidDate)
    } else if (dtReservationEndDate.isBeforeNow) {
      addError("reservationEndDate", ErrorCodes.dateTooSoon)
    } else if (DateUtils.isIsoDate(date) && dtReservationEndDate.isAfter(dtDate)) {
      addError("reservationEndDate", ErrorCodes.reservationEndDateTooLate)
    }

    if (name.isEmpty) {
      addError("name", ErrorCodes.emptyField)
    } else if (name.length > 250) {
      addError("name", ErrorCodes.bigString)
    }

    if (prices.isEmpty) {
      addError("prices", ErrorCodes.emptyField)
    } else {
      var mapErrors = Map[Int, ErrorItem]()
      for (i <- prices.indices) yield {
        val p = prices(i)
        if (!p.verify) {
          mapErrors = mapErrors + (i -> p.errors)
        }
      }
      if (mapErrors.nonEmpty) {
        addSeqErrors("prices", mapErrors)
      }
    }

    if (shows.isEmpty) {
      addError("shows", ErrorCodes.emptyField)
    }
  }
}

case class SessionFormBody(date: String, reservationEndDate: String, name: String, prices: Seq[SessionPriceForm], shows: Seq[String]) {
  def toSessionForm(theater: String): SessionForm = {
    SessionForm(theater, date, reservationEndDate, name, prices, shows)
  }
}

case class SessionPriceForm(name: String, price: Int, condition: Option[String]) extends VerifiableMultipleStructured {
  override protected def doVerify(): Unit = {
    if (name.isEmpty) {
      addError("name", ErrorCodes.emptyField)
    } else if (name.length > 250) {
      addError("name", ErrorCodes.bigString)
    }

    if (price < 0) {
      addError("price", ErrorCodes.negative)
    }

    if (condition exists { c => c.length > 250 }) {
      addError("condition", ErrorCodes.bigString)
    }
  }
}