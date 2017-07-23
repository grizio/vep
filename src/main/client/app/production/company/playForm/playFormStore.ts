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
  dates: FieldValidation<Array<DateValidation>>
  prices: FieldValidation<Array<PriceValidation>>
  errors?: Array<string>
}

export interface PriceValidation {
  name: FieldValidation<string>
  value: FieldValidation<number>
  condition: FieldValidation<string>
}

export interface DateValidation {
  date: FieldValidation<Date>
  reservationEndDate: FieldValidation<Date>
}

const defaultDateValidation: DateValidation = {
  date: defaultFieldValidation(null),
  reservationEndDate: defaultFieldValidation(null)
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
  dates: defaultFieldValidation([defaultDateValidation]),
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

  on(actions.addDate, (state) => {
    return updateDates(state, arrays.append(state.dates.value, defaultDateValidation))
  })

  on(actions.removeDate, (state, index) => {
    return updateDates(state, arrays.remove(state.dates.value, index))
  })

  on(actions.updateDate, (state, {index, value}) => {
    const currentDate = state.dates.value[index]
    return updateDates(state, arrays.replace(state.dates.value, index, copy(currentDate, {
      date: updateFieldValidation(currentDate.date, value, validateNotNull(value).flatMap(validateFuture)),
      reservationEndDate: updateUnchangedFieldValidation(currentDate.reservationEndDate, currentDate.reservationEndDate.value, validateReservationEndDate(currentDate.reservationEndDate.value, value))
    })))
  })

  on(actions.updateReservationEndDate, (state, {index, value}) => {
    const currentDate = state.dates.value[index]
    return updateDates(state, arrays.replace(state.dates.value, index, copy(currentDate, {
      reservationEndDate: updateFieldValidation(currentDate.reservationEndDate, value, validateReservationEndDate(value, currentDate.date.value))
    })))
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

function updateDates(state: PlayFormState, dates: Array<DateValidation>) {
  return copy(state, {
    dates: updateFieldValidation(state.dates, dates, validateDates(dates))
  })
}

function validateDates(dates: Array<DateValidation>) {
  return validateNonEmptyArray(dates)
}

function updatePrices(state: PlayFormState, prices: Array<PriceValidation>) {
  return copy(state, {
    prices: updateFieldValidation(state.prices, prices, validatePrices(prices))
  })
}

function validatePrices(prices: Array<PriceValidation>) {
  return validateNonEmptyArray(prices)
}