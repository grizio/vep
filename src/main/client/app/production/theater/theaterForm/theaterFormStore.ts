import {LocalStore} from "fluxx"
import * as actions from "./theaterFormActions"
import {
  defaultFieldValidation, FieldValidation, refreshValidation, updateFieldValidation
} from "../../../framework/utils/Validation"
import {validateNonBlank, validateNonEmptyArray, validatePositiveNumber} from "../../../common/commonValidations"
import {append, remove, replace} from "../../../framework/utils/arrays";
import {copy, notNullOrUndefined} from "../../../framework/utils/object";
import {Seat, SeatType} from "../theaterModel";

export interface TheaterFormState {
  step: "loading" | "form" | "success"
  id?: string
  name: FieldValidation<string>
  address: FieldValidation<string>
  content: FieldValidation<string>
  seats: FieldValidation<Array<SeatValidation>>
  selectedSeat?: number
  errors?: Array<string>
}

export interface SeatValidation {
  c: FieldValidation<string>
  x: FieldValidation<number>
  y: FieldValidation<number>
  w: FieldValidation<number>
  h: FieldValidation<number>
  t: FieldValidation<SeatType>
}

const initialState: TheaterFormState = {
  step: "loading",
  name: defaultFieldValidation(""),
  address: defaultFieldValidation(""),
  content: defaultFieldValidation(""),
  seats: defaultFieldValidation([])
}

const initialSeat: SeatValidation = seatToSeatValidation({c: "", x: 0, y: 0, w: 50, h: 50, t: "normal" as SeatType})

export const theaterFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, value) => {
    return copy(state, {
      step: "form",
      id: value.id,
      name: defaultFieldValidation(value.name),
      address: defaultFieldValidation(value.address),
      content: defaultFieldValidation(value.content),
      seats: defaultFieldValidation(value.seats.map(seatToSeatValidation))
    })
  })

  on(actions.initializeEmpty, (state) => {
    return copy(state, {step: "form"})
  })

  on(actions.updateName, (state, value) => {
    return copy(state, {
      name: updateFieldValidation(state.name, value, validateNonBlank(value))
    })
  })

  on(actions.updateAddress, (state, value) => {
    return copy(state, {
      address: updateFieldValidation(state.address, value, validateNonBlank(value))
    })
  })

  on(actions.updateContent, (state, value) => {
    return copy(state, {
      content: updateFieldValidation(state.content, value, validateNonBlank(value))
    })
  })

  on(actions.addSeat, (state) => {
    const newSeats = append(state.seats.value, createNewSeat(state))
    return copy(state, {
      seats: updateFieldValidation(state.seats, newSeats, validateNonEmptyArray(newSeats)),
      selectedSeat: state.seats.value.length
    })
  })

  on(actions.removeSeat, (state, index) => {
    const newSeats = remove(state.seats.value, index)
    return copy(state, {
      seats: updateFieldValidation(state.seats, newSeats, validateNonEmptyArray(newSeats)),
      selectedSeat: null
    })
  })

  on(actions.selectSeat, (state, seat) => {
    return copy(state, {
      selectedSeat: seat
    })
  })

  on(actions.updateSeatCode, (state, {index, value}) => {
    return replaceSeat(state, index, "c", value)
  })

  on(actions.updateSeatX, (state, {index, value}) => {
    return replaceSeat(state, index, "x", value)
  })

  on(actions.updateSeatY, (state, {index, value}) => {
    return replaceSeat(state, index, "y", value)
  })

  on(actions.updateSeatWidth, (state, {index, value}) => {
    return replaceSeat(state, index, "w", value)
  })

  on(actions.updateSeatHeight, (state, {index, value}) => {
    return replaceSeat(state, index, "h", value)
  })

  on(actions.updateSeatType, (state, {index, value}) => {
    return replaceSeat(state, index, "t", value)
  })

  on(actions.updateErrors, (state, value) => {
    return copy(state, {errors: value})
  })

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(state, {step: "success"})
  })
})

function createNewSeat(state: TheaterFormState) {
  if (notNullOrUndefined(state.selectedSeat)) {
    const seat = state.seats.value[state.selectedSeat]
    return copy(seat, {
      x: copy(seat.x, {value: seat.x.value + seat.w.value / 2}),
      y: copy(seat.y, {value: seat.y.value + seat.h.value / 2})
    })
  } else {
    return initialSeat
  }
}

function replaceSeat(state: TheaterFormState, index: number, field: string, value: any): TheaterFormState {
  return copy(state, {
    seats: copy(state.seats, {
      value: replace(state.seats.value, index, validateSeat(copySeat(state.seats.value[index], field, value)))
    })
  })
}

function copySeat(seat: SeatValidation, field: string, value: any): SeatValidation {
  return copy(seat, {
    [field]: copy(seat[field], {value, changed: true})
  })
}

function validateSeat(seat: SeatValidation): SeatValidation {
  return {
    c: refreshValidation(seat.c, validateNonBlank),
    x: refreshValidation(seat.x, validatePositiveNumber),
    y: refreshValidation(seat.y, validatePositiveNumber),
    w: refreshValidation(seat.w, validatePositiveNumber),
    h: refreshValidation(seat.h, validatePositiveNumber),
    t: refreshValidation(seat.t, (t) => validateNonBlank(t).map(_ => t))
  }
}

function seatToSeatValidation(seat: Seat): SeatValidation {
  return {
    c: defaultFieldValidation(seat.c),
    x: defaultFieldValidation(seat.x),
    y: defaultFieldValidation(seat.y),
    w: defaultFieldValidation(seat.w),
    h: defaultFieldValidation(seat.h),
    t: defaultFieldValidation(seat.t)
  }
}