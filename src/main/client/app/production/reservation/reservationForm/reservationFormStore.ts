import {LocalStore} from "fluxx"
import * as actions from "./reservationFormActions"
import {
  defaultFieldValidation,
  FieldValidation,
  updateFieldValidation,
  Valid,
  Validation
} from "../../../framework/utils/Validation";
import {copy} from "../../../framework/utils/object";
import * as arrays from "../../../framework/utils/arrays";
import {validateEmail, validateNonBlank} from "../../../common/commonValidations";
import messages from "../../../framework/messages";
import {reservationDeleted, reservationDone} from "../reservationActions";
import {findReservedSeats} from "../reservationApi";
import {PlayPrice} from "../../play/playModel";

export interface ReservationFormState {
  loading: boolean
  play: string
  reservedSeats: Array<string>
  firstName: FieldValidation<string>
  lastName: FieldValidation<string>
  email: FieldValidation<string>
  city: FieldValidation<string>
  newsletter: FieldValidation<boolean>
  comment: FieldValidation<string>
  seats: FieldValidation<Array<string>>
  prices: FieldValidation<Array<ReservationPriceValidation>>
  errors?: Array<string>
  success?: boolean
}

export interface ReservationPriceValidation {
  price: PlayPrice
  count: FieldValidation<number>
}

const initialState: ReservationFormState = {
  loading: true,
  play: null,
  reservedSeats: null,
  firstName: defaultFieldValidation(""),
  lastName: defaultFieldValidation(""),
  email: defaultFieldValidation(""),
  city: defaultFieldValidation(""),
  newsletter: defaultFieldValidation(false),
  comment: defaultFieldValidation(""),
  seats: defaultFieldValidation([]),
  prices: defaultFieldValidation([])
}

export const reservationFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, play) => {
    reloadReservedSeats(play.id)
    return copy(state, {
      play: play.id,
      prices: defaultFieldValidation(play.prices.map(_ => ({price: _, count: defaultFieldValidation(0)})))
    })
  })

  on(actions.reloadReservedSeats, (state, reservedSeats) =>
    copy(state, {reservedSeats})
  )

  on(actions.updateFirstName, (state, value) => {
    return copy(state, {
      firstName: updateFieldValidation(state.firstName, value, validateNonBlank(value))
    })
  })

  on(actions.updateLastName, (state, value) => {
    return copy(state, {
      lastName: updateFieldValidation(state.lastName, value, validateNonBlank(value))
    })
  })

  on(actions.updateEmail, (state, value) => {
    return copy(state, {
      email: updateFieldValidation(state.email, value, validateEmail(value))
    })
  })

  on(actions.updateCity, (state, value) => {
    return copy(state, {
      city: updateFieldValidation(state.city, value, Valid(value))
    })
  })

  on(actions.updateNewsletter, (state, value) => {
    return copy(state, {
      newsletter: updateFieldValidation(state.newsletter, value, Valid(value))
    })
  })

  on(actions.updateComment, (state, value) => {
    return copy(state, {
      comment: updateFieldValidation(state.comment, value, Valid(value))
    })
  })

  on(actions.updatePrice, (state, {price, value}) => {
    const newPrices = state.prices.value.map(statePrice => {
      if (statePrice.price.name === price) {
        return copy(statePrice, {
          count: updateFieldValidation(statePrice.count, value, Valid(value))
        })
      } else {
        return statePrice
      }
    })
    return copy(state, {
      prices: updateFieldValidation(state.prices, newPrices, verifyPriceRepartition(newPrices, state.seats.value))
    })
  })

  on(actions.toggleSeat, (state, seat) => {
    if (arrays.containsNot(state.reservedSeats, seat)) {
      if (arrays.contains(state.seats.value, seat)) {
        const newValue = state.seats.value.filter(_ => _ !== seat)
        return copy(state, {
          seats: updateFieldValidation(state.seats, newValue, verifySeats(newValue))
        })
      } else {
        const newValue = arrays.append(state.seats.value, seat)
        return copy(state, {
          seats: updateFieldValidation(state.seats, newValue, verifySeats(newValue))
        })
      }
    } else {
      return state
    }
  })

  on(actions.updateErrors, (state, errors) => {
    return copy(state, {errors})
  })

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(initialState, {
      loading: false,
      reservedSeats: arrays.appendAll(state.reservedSeats, state.seats.value),
      success: true,
      play: state.play
    })
  })

  on(actions.closeSuccess, (state) => {
    return copy(state, {success: false})
  })

  on(reservationDeleted, (state) => {
    reloadReservedSeats(state.play)
    return state
  })

  on(reservationDone, (state) => {
    reloadReservedSeats(state.play)
    return state
  })
})

function verifySeats(seats: Array<string>): Validation<Array<string>> {
  return Valid(seats)
    .filter(_ => _.length > 0, messages.production.reservation.form.noSeats)
}

function reloadReservedSeats(playId: string) {
  findReservedSeats(playId)
    .then(reservedSeats => actions.reloadReservedSeats(reservedSeats))
}

function verifyPriceRepartition(prices: Array<ReservationPriceValidation>, seats: Array<string>): Validation<Array<ReservationPriceValidation>> {
  return Valid(prices)
    .filter(
      _ => _.map(_ => _.count.value).reduce((x, y) => x + y) === seats.length,
      messages.production.reservation.form.invalidPriceRepartition
    )
}