import {LocalStore} from "fluxx"
import * as actions from "./companyFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation, Valid
} from "../../../framework/utils/Validation"
import {validateNonBlank} from "../../../common/commonValidations"
import {copy} from "../../../framework/utils/object";

export interface CompanyFormState {
  step: "loading" | "form" | "success"
  id?: string
  name: FieldValidation<string>
  address: FieldValidation<string>
  content: FieldValidation<string>
  isVep: FieldValidation<boolean>
  errors?: Array<string>
}

const initialState: CompanyFormState = {
  step: "loading",
  name: defaultFieldValidation(""),
  address: defaultFieldValidation(""),
  content: defaultFieldValidation(""),
  isVep: defaultFieldValidation(false)
}

export const companyFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, value) => {
    return copy(state, {
      step: "form",
      id: value.id,
      name: defaultFieldValidation(value.name),
      address: defaultFieldValidation(value.address),
      isVep: defaultFieldValidation(value.isVep),
      content: defaultFieldValidation(value.content)
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

  on(actions.updateIsVep, (state, value) => {
    return copy(state, {
      isVep: updateFieldValidation(state.isVep, value, Valid(value))
    })
  })

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(state, {step: "success"})
  })
})