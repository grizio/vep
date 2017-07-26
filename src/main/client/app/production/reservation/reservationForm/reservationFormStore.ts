import {LocalStore} from "fluxx"
import * as actions from "./reservationFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation,
  Valid, Validation
} from "../../../framework/utils/Validation";
import {copy} from "../../../framework/utils/object";
import * as arrays from "../../../framework/utils/arrays";
import {validateEmail, validateNonBlank} from "../../../common/commonValidations";
import messages from "../../../framework/messages";

export interface ReservationFormState {
  loading: boolean
  reservedSeats: Array<string>
  firstName: FieldValidation<string>
  lastName: FieldValidation<string>
  email: FieldValidation<string>
  city: FieldValidation<string>
  comment: FieldValidation<string>
  seats: FieldValidation<Array<string>>
  errors?: Array<string>
  success?: boolean
}

const initialState: ReservationFormState = {
  loading: true,
  reservedSeats: null,
  firstName: defaultFieldValidation(""),
  lastName: defaultFieldValidation(""),
  email: defaultFieldValidation(""),
  city: defaultFieldValidation(""),
  comment: defaultFieldValidation(""),
  seats: defaultFieldValidation([])
}

export const reservationFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {reservedSeats}) => {
    return copy(state, {reservedSeats})
  })

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

  on(actions.updateComment, (state, value) => {
    return copy(state, {
      comment: updateFieldValidation(state.comment, value, Valid(value))
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
      success: true
    })
  })

  on(actions.closeSuccess, (state) => {
    return copy(state, {success: false})
  })
})

function verifySeats(seats: Array<string>): Validation<Array<string>> {
  return Valid(seats)
    .filter(_ => _.length > 0, messages.production.reservation.form.noSeats)
}