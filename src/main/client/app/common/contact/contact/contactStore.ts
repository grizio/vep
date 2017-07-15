import {LocalStore} from "fluxx"
import {
  defaultFieldValidation, FieldValidation, Invalid, updateFieldValidation, updateUnchangedFieldValidation, Valid,
  Validation
} from "../../../framework/utils/Validation"
import {validateEmail, validateNonBlank} from "../../commonValidations"
import * as actions from "./contactActions"
import messages from "../../../framework/messages";

export interface ContactState {
  step: "form" | "success"
  name: FieldValidation<string>
  email: FieldValidation<string>
  email2: FieldValidation<string>
  title: FieldValidation<string>
  content: FieldValidation<string>
  errors?: Array<string>
}

const initialState: ContactState = {
  step: "form",
  name: defaultFieldValidation(""),
  email: defaultFieldValidation(""),
  email2: defaultFieldValidation(""),
  title: defaultFieldValidation(""),
  content: defaultFieldValidation("")
}

export const contactStore = () => LocalStore(initialState, on => {
  on(actions.updateName, (state, value) => {
    return Object.assign({}, state, {
      name: updateFieldValidation(state.name, value, validateNonBlank(value))
    })
  })

  on(actions.updateEmail, (state, value) => {
    return Object.assign({}, state, {
      email: updateFieldValidation(state.email, value, validateEmail(value)),
      email2: updateUnchangedFieldValidation(state.email2, state.email2.value, validateEmail2(value, state.email2.value))
    })
  })

  on(actions.updateEmail2, (state, value) => {
    return Object.assign({}, state, {
      email2: updateFieldValidation(state.email2, value, validateEmail2(state.email.value, value))
    })
  })

  on(actions.updateTitle, (state, value) => {
    return Object.assign({}, state, {
      title: updateFieldValidation(state.title, value, validateNonBlank(value))
    })
  })

  on(actions.updateContent, (state, value) => {
    return Object.assign({}, state, {
      content: updateFieldValidation(state.content, value, validateNonBlank(value))
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

function validateEmail2(email1: string, email2: string): Validation<string> {
  if (email1 === email2) {
    return Valid(email2)
  } else {
    return Invalid([messages.common.contact.differentEmail])
  }
}