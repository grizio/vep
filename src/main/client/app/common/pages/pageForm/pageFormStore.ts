import {LocalStore} from "fluxx"
import * as actions from "./pageFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation, Valid
} from "../../../framework/utils/Validation"
import {copy} from "../../../framework/utils/object";
import {validateCanonical, validateNonBlank} from "../../commonValidations";

export interface PageFormState {
  step: "loading" | "form" | "success"
  canonical: FieldValidation<string>
  title: FieldValidation<string>
  order: FieldValidation<number>
  content: FieldValidation<string>
  errors?: Array<string>
}

const initialState: PageFormState = {
  step: "loading",
  canonical: defaultFieldValidation(""),
  title: defaultFieldValidation(""),
  order: defaultFieldValidation(null),
  content: defaultFieldValidation("")
}

export const pageFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, page) => {
    return copy(state, {
      step: "form",
      canonical: defaultFieldValidation(page.canonical),
      title: defaultFieldValidation(page.title),
      order: defaultFieldValidation(page.order),
      content: defaultFieldValidation(page.content)
    })
  })

  on(actions.initializeEmpty, (state) => {
    return copy(state, {step: "form"})
  })

  on(actions.updateCanonical, (state, value) => {
    return copy(state, {
      canonical: updateFieldValidation(state.canonical, value, validateCanonical(value))
    })
  })

  on(actions.updateTitle, (state, value) => {
    return copy(state, {
      title: updateFieldValidation(state.title, value, validateNonBlank(value))
    })
  })

  on(actions.updateOrder, (state, value) => {
    return copy(state, {
      order: updateFieldValidation(state.order, value, Valid(value))
    })
  })

  on(actions.updateContent, (state, value) => {
    return copy(state, {
      content: updateFieldValidation(state.content, value, validateNonBlank(value))
    })
  })

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(state, {step: "success"})
  })
})