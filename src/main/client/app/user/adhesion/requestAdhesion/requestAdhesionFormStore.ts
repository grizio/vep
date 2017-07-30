import {LocalStore} from "fluxx"
import * as actions from "./requestAdhesionFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation, Valid
} from "../../../framework/utils/Validation"
import {copy} from "../../../framework/utils/object";
import * as arrays from "../../../framework/utils/arrays";
import {validateNonBlank, validateNonEmptyArray, validatePassed} from "../../../common/commonValidations";
import {PeriodAdhesion} from "../adhesionModel";
import {Function1} from "../../../framework/lib";

export interface RequestAdhesionFormState {
  step: "loading" | "form" | "success"
  acceptedPeriods: Array<PeriodAdhesion>
  period: FieldValidation<string>
  members: FieldValidation<Array<AdhesionMemberValidation>>,
  errors?: Array<string>
}

export interface AdhesionMemberValidation {
  firstName: FieldValidation<string>
  lastName: FieldValidation<string>
  birthday: FieldValidation<Date>
  activity: FieldValidation<string>
}

const defaultMember: AdhesionMemberValidation = {
  firstName: defaultFieldValidation(""),
  lastName: defaultFieldValidation(""),
  birthday: defaultFieldValidation(null),
  activity: defaultFieldValidation(null),
}

const initialState: RequestAdhesionFormState = {
  step: "loading",
  acceptedPeriods: [],
  period: defaultFieldValidation(null),
  members: defaultFieldValidation([defaultMember])
}

export const requestAdhesionFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, {acceptedPeriods}) => {
    return copy(state, {step: "form", acceptedPeriods})
  })

  on(actions.updatePeriod, (state, value) => {
    return copy(state, {
      period: updateFieldValidation(state.period, value, Valid(value))
    })
  })

  on(actions.addMember, (state) => {
    const newMembers = arrays.append(state.members.value, defaultMember)
    return copy(state, {
      members: updateFieldValidation(state.members, newMembers, validateMembers(newMembers))
    })
  })

  on(actions.removeMember, (state, index) => {
    const newMembers = arrays.remove(state.members.value, index)
    return copy(state, {
      members: updateFieldValidation(state.members, newMembers, validateMembers(newMembers))
    })
  })

  on(actions.updateMemberFirstName, (state, {index, value}) =>
    updateMember(state, index, member => copy(member, {
      firstName: updateFieldValidation(member.firstName, value, validateNonBlank(value))
    }))
  )

  on(actions.updateMemberLastName, (state, {index, value}) =>
    updateMember(state, index, member => copy(member, {
      lastName: updateFieldValidation(member.lastName, value, validateNonBlank(value))
    }))
  )

  on(actions.updateMemberBirthday, (state, {index, value}) =>
    updateMember(state, index, member => copy(member, {
      birthday: updateFieldValidation(member.birthday, value, validatePassed(value))
    }))
  )

  on(actions.updateMemberActivity, (state, {index, value}) =>
    updateMember(state, index, member => copy(member, {
      activity: updateFieldValidation(member.activity, value, Valid(value))
    }))
  )

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(state, {step: "success"})
  })
})

function updateMember(state: RequestAdhesionFormState, index: number, update: Function1<AdhesionMemberValidation, AdhesionMemberValidation>) {
  const members = state.members
  const oldMember = members.value[index]
  const newMember = update(oldMember)
  const newMembers = arrays.replace(members.value, index, newMember)
  return copy(state, {
    members: updateFieldValidation(members, newMembers, validateMembers(newMembers))
  })
}

function validateMembers(members: Array<AdhesionMemberValidation>) {
  return validateNonEmptyArray(members)
}