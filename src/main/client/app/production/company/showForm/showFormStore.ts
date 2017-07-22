import {LocalStore} from "fluxx"
import * as actions from "./showFormActions"
import {defaultFieldValidation, FieldValidation, updateFieldValidation} from "../../../framework/utils/Validation"
import {validateNonBlank} from "../../../common/commonValidations"
import {copy} from "../../../framework/utils/object";
import {Company} from "../companyModel";

export interface ShowFormState {
  step: "loading" | "form" | "success"
  company: Company,
  id?: string
  title: FieldValidation<string>
  author: FieldValidation<string>
  director: FieldValidation<string>
  content: FieldValidation<string>
  errors?: Array<string>
}

const initialState: ShowFormState = {
  step: "loading",
  company: null,
  title: defaultFieldValidation(""),
  author: defaultFieldValidation(""),
  director: defaultFieldValidation(""),
  content: defaultFieldValidation("")
}

export const showFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {company, show}) => {
    if (show) {
      return copy(state, {
        step: "form",
        company,
        id: show.id,
        title: defaultFieldValidation(show.title),
        author: defaultFieldValidation(show.author),
        director: defaultFieldValidation(show.director),
        content: defaultFieldValidation(show.content)
      })
    } else {
      return copy(state, {step: "form", company})
    }
  })

  on(actions.updateTitle, (state, value) => {
    return copy(state, {
      title: updateFieldValidation(state.title, value, validateNonBlank(value))
    })
  })

  on(actions.updateAuthor, (state, value) => {
    return copy(state, {
      author: updateFieldValidation(state.author, value, validateNonBlank(value))
    })
  })

  on(actions.updateDirector, (state, value) => {
    return copy(state, {
      director: updateFieldValidation(state.director, value, validateNonBlank(value))
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