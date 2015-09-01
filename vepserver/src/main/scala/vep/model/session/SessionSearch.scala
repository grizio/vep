package vep.model.session

import org.joda.time.DateTime
import vep.model.common.{ErrorCodes, VerifiableMultiple}
import vep.utils.DateUtils

case class SessionSearch(t: Option[String], s: Option[String], sd: Option[String], ed: Option[String], o: Option[String], p: Option[Int]) extends VerifiableMultiple {
  def theater = t

  def show = s

  def startDate = sd

  def endDate = ed

  lazy val dtStartDate = startDate.map(DateUtils.toDateTime)
  lazy val dtEndDate = endDate.map(DateUtils.toDateTime)

  def order = o

  def page = p

  override protected def doVerify(): Unit = {
    if (page exists (_ <= 0)) {
      addError("p", ErrorCodes.negativeOrNull)
    }

    if (order exists (!SessionSearchColumns.validOrderColumns.contains(_))) {
      addError("o", ErrorCodes.unknownOrder)
    }

    if (startDate exists DateUtils.isNotIsoDate) {
      addError("sd", ErrorCodes.invalidDate)
    }

    if (endDate exists DateUtils.isNotIsoDate) {
      addError("ed", ErrorCodes.invalidDate)
    }
  }
}

object SessionSearchColumns {
  lazy val orderColumn = Map(
    "default" -> "date",
    "d" -> "date",
    "t" -> "theaterName",
    "s" -> "shows"
  )
  lazy val validOrderColumns = orderColumn.keySet
}

case class SessionSearchParsed(theater: String, theaterName: String, id: Int, canonical: String, date: DateTime)

case class SessionSearchShow(canonical: String, title: String)

case class SessionSearchResult(canonical: String, theater: String, theaterName: String, shows: Seq[SessionSearchShow], date: DateTime)

case class SessionSearchResponse(sessions: Seq[SessionSearchResult], pageMax: Int)