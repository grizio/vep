import {LocalStore} from "fluxx"
import * as actions from "./resetPasswordActions"
import {
  defaultFieldValidation, FieldValidation, Invalid, updateFieldValidation,
  updateUnchangedFieldValidation, Valid, Validation
} from "../../../framework/utils/Validation"
import {validatePassword} from "../../../common/commonValidations"
import messages from "../../../framework/messages";

export interface ResetPasswordState {
  step: "form" | "success"
  password: FieldValidation<string>
  password2: FieldValidation<string>
  errors?: Array<string>
}

const initialState: ResetPasswordState = {
  step: "form",
  password: defaultFieldValidation(""),
  password2: defaultFieldValidation("")
}

export const resetPasswordStore = () => LocalStore(initialState, on => {
  on(actions.updatePassword, (state, value) => {
    return Object.assign({}, state, {
      password: updateFieldValidation(state.password, value, validatePassword(value)),
      password2: updateUnchangedFieldValidation(state.password2, state.password2.value, validatePassword2(value, state.password2.value))
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