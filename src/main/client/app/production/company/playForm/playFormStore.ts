import {LocalStore} from "fluxx"
import * as actions from "./playFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation,
  updateUnchangedFieldValidation, Valid
} from "../../../framework/utils/Validation"
import {
  validateFuture, validateNonBlank, validateNonEmptyArray,
  validateNotNull, validatePositiveNumber
} from "../../../common/commonValidations"
import {copy} from "../../../framework/utils/object";
import {Company, Show} from "../companyModel";
import {Theater} from "../../theater/theaterModel";
import messages from "../../../framework/messages";
import * as arrays from "../../../framework/utils/arrays";

export interface PlayFormState {
  step: "loading" | "form" | "success"
  company: Company
  show: Show
  availableTheaters: Array<Theater>
  id?: string
  theater: FieldValidation<string>
  date: FieldValidation<Date>
  reservationEndDate: FieldValidation<Date>
  prices: FieldValidation<Array<PriceValidation>>
  errors?: Array<string>
}

export interface PriceValidation {
  name: FieldValidation<string>
  value: FieldValidation<number>
  condition: FieldValidation<string>
}

const defaultPriceValidation: PriceValidation = {
  name: defaultFieldValidation(""),
  value: defaultFieldValidation(0),
  condition: defaultFieldValidation("")
}

const initialState: PlayFormState = {
  step: "loading",
  company: null,
  show: null,
  availableTheaters: [],
  theater: defaultFieldValidation(""),
  date: defaultFieldValidation(null),
  reservationEndDate: defaultFieldValidation(null),
  prices: defaultFieldValidation([defaultPriceValidation])
}

export const playFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {company, show, theaters}) => {
    return copy(state, {step: "form", company, show, availableTheaters: theaters})
  })

  on(actions.updateTheater, (state, value) => {
    return copy(state, {
      theater: updateFieldValidation(state.theater, value, validateNonBlank(value))
    })
  })

  on(actions.updateDate, (state, value) => {
    return copy(state, {
      date: updateFieldValidation(state.date, value, validateNotNull(value).flatMap(validateFuture)),
      reservationEndDate: updateUnchangedFieldValidation(state.reservationEndDate, state.reservationEndDate.value, validateReservationEndDate(state.reservationEndDate.value, value))
    })
  })

  on(actions.updateReservationEndDate, (state, value) => {
    return copy(state, {
      reservationEndDate: updateFieldValidation(state.reservationEndDate, value, validateReservationEndDate(value, state.date.value))
    })
  })

  on(actions.addPrice, (state) => {
    return updatePrices(state, arrays.append(state.prices.value, defaultPriceValidation))
  })

  on(actions.removePrice, (state, index) => {
    return updatePrices(state, arrays.remove(state.prices.value, index))
  })

  on(actions.updatePriceName, (state, {index, value}) => {
    const currentPrice = state.prices.value[index]
    return updatePrices(state, arrays.replace(state.prices.value, index, copy(currentPrice, {
      name: updateFieldValidation(currentPrice.name, value, validateNonBlank(value))
    })))
  })

  on(actions.updatePriceValue, (state, {index, value}) => {
    const currentPrice = state.prices.value[index]
    return updatePrices(state, arrays.replace(state.prices.value, index, copy(currentPrice, {
      value: updateFieldValidation(currentPrice.value, value, validatePositiveNumber(value))
    })))
  })

  on(actions.updatePriceCondition, (state, {index, value}) => {
    const currentPrice = state.prices.value[index]
    return updatePrices(state, arrays.replace(state.prices.value, index, copy(currentPrice, {
      condition: updateFieldValidation(currentPrice.condition, value, Valid(value))
    })))
  })

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(state, {step: "success"})
  })
})

function validateReservationEndDate(reservationEndDate: Date, date: Date) {
  return validateNotNull(reservationEndDate)
    .flatMap(validateFuture)
    .filter(reservationEndDate => reservationEndDate < date, messages.production.company.show.play.form.reservationEndDateAfterDate)
}

function updatePrices(state: PlayFormState, prices: Array<PriceValidation>) {
  return copy(state, {
    prices: updateFieldValidation(state.prices, prices, validatePrices(prices))
  })
}

function validatePrices(prices: Array<PriceValidation>) {
  return validateNonEmptyArray(prices)
}