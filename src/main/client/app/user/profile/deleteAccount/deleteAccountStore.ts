import { LocalStore } from 'fluxx'
import { validateNonBlank } from '../../../common/commonValidations'
import { copy } from '../../../framework/utils/object'
import { defaultFieldValidation, FieldValidation, updateFieldValidation } from '../../../framework/utils/Validation'
import { Profile } from '../profileModel'
import * as actions from './deleteAccountActions'

export interface DeleteAccountState {
  profile: Profile
  password: FieldValidation<string>
  errors?: Array<string>
}

const initialState: DeleteAccountState = {
  profile: null,
  password: defaultFieldValidation('')
}

export const deleteAccountStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, profile) => {
    return copy(state, { profile })
  })

  on(actions.updatePassword, (state, password) => {
    return copy(state, {
      password: updateFieldValidation(state.password, password, validateNonBlank(password))
    })
  })

  on(actions.updateErrors, (state, errors) => {
    return copy(state, { errors })
  })

  on(actions.closeErrors, (state) => {
    return copy(state, { errors: null })
  })
})