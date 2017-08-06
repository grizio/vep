import {LocalStore} from "fluxx"
import * as actions from "./blogFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation
} from "../../../framework/utils/Validation"
import {copy} from "../../../framework/utils/object";
import {validateNonBlank} from "../../commonValidations";

export interface BlogFormState {
  step: "loading" | "form" | "success"
  title: FieldValidation<string>
  content: FieldValidation<string>
  errors?: Array<string>
}

const initialState: BlogFormState = {
  step: "loading",
  title: defaultFieldValidation(""),
  content: defaultFieldValidation("")
}

export const blogFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, blog) => {
    return copy(state, {
      step: "form",
      title: defaultFieldValidation(blog.title),
      content: defaultFieldValidation(blog.content)
    })
  })

  on(actions.initializeEmpty, (state) => {
    return copy(state, {step: "form"})
  })

  on(actions.updateTitle, (state, value) => {
    return copy(state, {
      title: updateFieldValidation(state.title, value, validateNonBlank(value))
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