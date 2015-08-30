package vep.test.controller.session

import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification
import vep.model.session._

trait SessionMatcher {
  self: Specification =>

  def matchSession(value: Session, expected: Session) =
    (value.theater === expected.theater) and
      (value.date === expected.date) and
      (value.name === expected.name) and
      (value.reservationEndDate === expected.reservationEndDate)

  def equalSession(value: Session, expected: Session) =
    (value.theater == expected.theater) &&
      (value.date == expected.date) &&
      (value.name == expected.name) &&
      (value.reservationEndDate == expected.reservationEndDate)

  def matchSessionPrice(value: SessionPrice, expected: SessionPrice) =
    (value.session === expected.session) and
      (value.name === expected.name) and
      (value.price === expected.price) and
      (value.cases === expected.cases)

  def equalSessionPrice(value: SessionPrice, expected: SessionPrice) =
    (value.session == expected.session) &&
      (value.name == expected.name) &&
      (value.price == expected.price) &&
      (value.cases == expected.cases)

  def matchSessionShow(value: SessionShow, expected: SessionShow) =
    (value.session === expected.session) and
      (value.show === expected.show) and
      (value.num === expected.num)

  def equalSessionShow(value: SessionShow, expected: SessionShow) =
    (value.session == expected.session) &&
      (value.show == expected.show) &&
      (value.num == expected.num)

  def matchSessionPriceSeq(value: Seq[SessionPrice], expected: Seq[SessionPrice]) =
    (value must have size expected.length) and
      (value must containTheSameElementsAs(expected, equalSessionPrice))

  def matchSessionShowSeq(value: Seq[SessionShow], expected: Seq[SessionShow]) =
    (value must have size expected.length) and
      (value must containTheSameElementsAs(expected, equalSessionShow))

  def matchSessionDetail(sessionUpdateForm: SessionUpdateForm): Matcher[SessionDetail] = { session: SessionDetail =>
    (session.date === sessionUpdateForm.dtDate) and
      (session.reservationEndDate === sessionUpdateForm.dtReservationEndDate) and
      (session.name === sessionUpdateForm.name) and
      (session.prices must matchSessionPriceSeq(sessionUpdateForm.prices)) and
      (session.shows === sessionUpdateForm.shows)
  }

  def matchSessionPriceSeq(sessionPriceFormSeq: Seq[SessionPriceForm]): Matcher[Seq[SessionPriceDetail]] = { sessionPriceSeq: Seq[SessionPriceDetail] =>
    sessionPriceSeq must contain[SessionPriceForm, SessionPriceDetail](matchSessionPriceForm)(sessionPriceFormSeq)
  }

  def matchSessionPriceForm(sessionPriceForm: SessionPriceForm): Matcher[SessionPriceDetail] = { sessionPrice: SessionPriceDetail =>
    (sessionPrice.name === sessionPriceForm.name) and
      (sessionPrice.price === sessionPriceForm.price) and
      (sessionPrice.cases === sessionPriceForm.condition)
  }
}
