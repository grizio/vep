import {LocalStore} from "fluxx"
import * as actions from "./periodAdhesionFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation
} from "../../../framework/utils/Validation"
import {copy} from "../../../framework/utils/object";
import * as arrays from "../../../framework/utils/arrays";
import {PeriodValidation, updatePeriodValidationEnd, updatePeriodValidationStart} from "../../../common/types/Period";
import {validateNonBlank, validateNonEmptyArray} from "../../../common/commonValidations";

export interface PeriodAdhesionFormState {
  step: "loading" | "form" | "success"
  period: FieldValidation<PeriodValidation>,
  registrationPeriod: FieldValidation<PeriodValidation>
  activities: FieldValidation<Array<FieldValidation<string>>>
  errors?: Array<string>
}

const initialState: PeriodAdhesionFormState = {
  step: "loading",
  period: defaultFieldValidation({start: defaultFieldValidation(null), end: defaultFieldValidation(null)}),
  registrationPeriod: defaultFieldValidation({start: defaultFieldValidation(null), end: defaultFieldValidation(null)}),
  activities: defaultFieldValidation([defaultFieldValidation("")])
}

export const periodAdhesionFormStore = () => LocalStore(initialState, on => {
  on(actions.initializeEmpty, (state) => {
    return copy(state, {step: "form"})
  })

  on(actions.updateStart, (state, value) => {
    return copy(state, {
      period: updatePeriodValidationStart(state.period, value)
    })
  })

  on(actions.updateEnd, (state, value) => {
    return copy(state, {
      period: updatePeriodValidationEnd(state.period, value)
    })
  })

  on(actions.updateRegistrationStart, (state, value) => {
    return copy(state, {
      registrationPeriod: updatePeriodValidationStart(state.registrationPeriod, value)
    })
  })

  on(actions.updateRegistrationEnd, (state, value) => {
    return copy(state, {
      registrationPeriod: updatePeriodValidationEnd(state.registrationPeriod, value)
    })
  })

  on(actions.addActivity, (state) => {
    const newActivities = arrays.append(state.activities.value, defaultFieldValidation(""));
    return copy(state, {
      activities: updateFieldValidation(state.activities, newActivities, validateNonEmptyArray(newActivities))
    })
  })

  on(actions.updateActivity, (state, {index, value}) => {
    const oldActivity = state.activities.value[index]
    const newActivity = updateFieldValidation(oldActivity, value, validateNonBlank(value))
    const newActivities = arrays.replace(state.activities.value, index, newActivity);
    return copy(state, {
      activities: updateFieldValidation(state.activities, newActivities, validateNonEmptyArray(newActivities))
    })
  })

  on(actions.removeActivity, (state, index) => {
    const newActivities = arrays.remove(state.activities.value, index);
    return copy(state, {
      activities: updateFieldValidation(state.activities, newActivities, validateNonEmptyArray(newActivities))
    })
  })

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(state, {step: "success"})
  })
})