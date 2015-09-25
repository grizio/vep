package vep.test.controller.session

import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification
import vep.model.session._

trait ReservationMatcher {
  self: Specification =>

  def matchReservation(value: Reservation, expected: Reservation) =
    (value.session === expected.session) and
      (value.firstName === expected.lastName) and
      (value.email === expected.email) and
      (value.city === expected.city) and
      (value.comment === expected.comment) and
      (value.seats === expected.seats)

  def equalReservation(value: Reservation, expected: Reservation) =
    (value.session == expected.session) &&
      (value.firstName == expected.lastName) &&
      (value.email == expected.email) &&
      (value.city == expected.city) &&
      (value.comment == expected.comment) &&
      (value.seats == expected.seats)

  def matchReservationPrice(value: ReservationPrice, expected: ReservationPrice) =
    (value.reservation === expected.reservation) and
      (value.price === expected.price) and
      (value.number === expected.number)

  def equalReservationPrice(value: ReservationPrice, expected: ReservationPrice) =
    (value.reservation == expected.reservation) &&
      (value.price == expected.price) &&
      (value.number == expected.number)

  def matchReservationSeat(value: ReservationSeat, expected: ReservationSeat) =
    (value.reservation === expected.reservation) and
      (value.seat === expected.seat)

  def equalReservationSeat(value: ReservationSeat, expected: ReservationSeat) =
    (value.reservation === expected.reservation) &&
      (value.seat === expected.seat)

  def matchReservationPriceSeq(value: Seq[ReservationPrice], expected: Seq[ReservationPrice]) =
    (value must have size expected.length) and
      (value must containTheSameElementsAs(expected, equalReservationPrice))

  def matchReservationSeatSeq(value: Seq[ReservationSeat], expected: Seq[ReservationSeat]) =
    (value must have size expected.length) and
      (value must containTheSameElementsAs(expected, equalReservationSeat))

  def matchReservationPriceFormSeq(reservationPriceFormSeq: Seq[ReservationPriceForm]): Matcher[Seq[ReservationPriceDetail]] = {
    reservationPriceSeq: Seq[ReservationPriceDetail] =>
    reservationPriceSeq must contain[ReservationPriceForm, ReservationPriceDetail](matchReservationPriceForm)(reservationPriceFormSeq)
  }

  def matchReservationPriceForm(reservationPriceForm: ReservationPriceForm): Matcher[ReservationPriceDetail] = { reservationPrice: ReservationPriceDetail =>
    (reservationPrice.price === reservationPriceForm.price) and
      (reservationPrice.number === reservationPriceForm.number)
  }

  def matchReservationDetail(reservationForm: ReservationForm): Matcher[ReservationDetail] = { reservationDetail: ReservationDetail =>
    (reservationDetail.session === reservationForm.session) and
      (reservationDetail.firstName === reservationForm.firstName) and
      (reservationDetail.lastName === reservationForm.lastName) and
      (reservationDetail.email === reservationForm.email) and
      (reservationDetail.city === reservationForm.city) and
      (reservationDetail.comment === reservationForm.comment) and
      (reservationDetail.seats === reservationForm.seats) and
      (reservationDetail.seatList === reservationForm.seatList) and
      (reservationDetail.prices must matchReservationPriceFormSeq(reservationForm.prices))
  }
}
