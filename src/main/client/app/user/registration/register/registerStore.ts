import {LocalStore} from "fluxx"
import * as actions from "./registerActions"
import {validateEmail, validatePassword} from "../../../common/commonValidations"
import {
  defaultFieldValidation, FieldValidation, Invalid, updateFieldValidation,
  updateUnchangedFieldValidation, Valid, Validation
} from "../../../framework/utils/Validation"
import messages from "../../../framework/messages"

export interface RegisterState {
  step: "form" | "success"
  email: FieldValidation<string>
  password: FieldValidation<string>
  password2: FieldValidation<string>
  errors?: Array<string>
}

const initialState: RegisterState = {
  step: "form",
  email: defaultFieldValidation(""),
  password: defaultFieldValidation(""),
  password2: defaultFieldValidation("")
}

export const registerStore = () => LocalStore(initialState, on => {
  on(actions.updateEmail, (state, value) => {
    return Object.assign({}, state, {
      email: updateFieldValidation(state.email, value, validateEmail(value))
    })
  })

  on(actions.updatePassword, (state, value) => {
    return Object.assign({}, state, {
      password: updateFieldValidation(state.password, value, validatePassword(value)),
      password2: updateUnchangedFieldValidation(state.password2, state.password2.value, validatePassword2(state.password.value, value))
    })
  })

  on(actions.updatePassword2, (state, value) => {
    return Object.assign({}, state, {
      password2: updateFieldValidation(state.password2, value, validatePassword2(state.password.value, value))
    })
  })

  on(actions.updateErrors, (state, value) => {
    return Object.assign({}, state, {errors: value})
  })

  on(actions.closeErrors, (state) => {
    return Object.assign({}, state, {errors: null})
  })

  on(actions.success, (state) => {
    return Object.assign({}, state, {step: "success"})
  })
})

function validatePassword2(password1: string, password2: string): Validation<string> {
  if (password1 === password2) {
    return Valid(password2)
  } else {
    return Invalid([messages.user.differentPassword])
  }
}