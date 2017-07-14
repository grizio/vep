import {LocalStore} from "fluxx"
import * as actions from "./loginActions"
import {defaultFieldValidation, FieldValidation, updateFieldValidation} from "../../../framework/utils/Validation"
import {validateEmail, validatePassword} from "../../../common/commonValidations"

export interface LoginState {
  step: "form" | "success"
  email: FieldValidation<string>
  password: FieldValidation<string>
  errors?: Array<string>
}

const initialState: LoginState = {
  step: "form",
  email: defaultFieldValidation(""),
  password: defaultFieldValidation("")
}

export const loginStore = () => LocalStore(initialState, on => {
  on(actions.updateEmail, (state, value) => {
    return Object.assign({}, state, {
      email: updateFieldValidation(state.email, value, validateEmail(value))
    })
  })

  on(actions.updatePassword, (state, value) => {
    return Object.assign({}, state, {
      password: updateFieldValidation(state.password, value, validatePassword(value))
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