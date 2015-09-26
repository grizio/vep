package vep.model.session

import vep.model.JsonImplicits

object ReservationImplicits extends JsonImplicits {
  implicit val impReservationPriceForm = jsonFormat2(ReservationPriceForm.apply)

  implicit val impReservationFormBody = jsonFormat8(ReservationFormBody.apply)

  implicit val impReservationFormResult = jsonFormat2(ReservationFormResult)

  implicit val impReservationPriceDetail = jsonFormat3(ReservationPriceDetail.apply)

  implicit val impReservationDetail = jsonFormat11(ReservationDetail.apply)

  implicit val impReservationDetailSeq = jsonFormat1(ReservationDetailSeq.apply)

  implicit val impReservedSeats = jsonFormat1(ReservedSeats.apply)
}
