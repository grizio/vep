package vep.model.session

import org.joda.time.DateTime

case class Session(id: Int, theater: Int, canonical: String, date: DateTime, name: String, reservationEndDate: DateTime)

case class SessionPrice(id: Int, session: Int, name: String, price: Int, cases: Option[String])

case class SessionShow(session: Int, show: Int, num: Int)