package vep.model.session

import org.joda.time.DateTime

case class Session(id: Int, theater: Int, canonical: String, date: DateTime, name: String, reservationEndDate: DateTime)

case class SessionPrice(id: Int, session: Int, name: String, price: Int, cases: Option[String])

case class SessionShow(session: Int, show: Int, num: Int)

case class SessionDetailParsed(id: Int, theater: String, canonical: String, date: DateTime, name: String, reservationEndDate: DateTime) {
  def toSessionDetail(prices: Seq[SessionPriceDetail], shows: Seq[String]) =
    SessionDetail(theater, canonical, date, name, reservationEndDate, prices, shows)
}

case class SessionDetail(theater: String, canonical: String, date: DateTime, name: String, reservationEndDate: DateTime,
                         prices: Seq[SessionPriceDetail], shows: Seq[String])

case class SessionPriceDetail(id: Int, name: String, price: Int, condition: Option[String])