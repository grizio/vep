import {LocalStore} from "fluxx"
import * as actions from "./profileFormActions"
import {
  defaultFieldValidation, FieldValidation, updateFieldValidation, Valid, Validation
} from "../../../framework/utils/Validation"
import {copy} from "../../../framework/utils/object";
import {validateNonBlank, validateNonEmptyArray} from "../../../common/commonValidations";
import * as arrays from "../../../framework/utils/arrays";
import {Phone} from "../profileModel";

export interface ProfileFormState {
  step: "loading" | "form" | "success"
  email: string
  firstName: FieldValidation<string>
  lastName: FieldValidation<string>
  address: FieldValidation<string>
  zipCode: FieldValidation<string>
  city: FieldValidation<string>
  phones: FieldValidation<Array<PhoneValidation>>
  errors?: Array<string>
}

export interface PhoneValidation {
  name: FieldValidation<string>
  number: FieldValidation<string>
}

const defaultPhone: PhoneValidation = {
  name: defaultFieldValidation(""),
  number: defaultFieldValidation("")
}

const initialState: ProfileFormState = {
  step: "loading",
  email: null,
  firstName: defaultFieldValidation(""),
  lastName: defaultFieldValidation(""),
  address: defaultFieldValidation(""),
  zipCode: defaultFieldValidation(""),
  city: defaultFieldValidation(""),
  phones: defaultFieldValidation([defaultPhone])
}

export const profileFormStore = () => LocalStore(initialState, on => {
  on(actions.initialize, (state, profile) => {
    return copy(state, {
      step: "form",
      email: profile.email,
      firstName: defaultFieldValidation(profile.firstName),
      lastName: defaultFieldValidation(profile.lastName),
      address: defaultFieldValidation(profile.address),
      zipCode: defaultFieldValidation(profile.zipCode),
      city: defaultFieldValidation(profile.city),
      phones: defaultFieldValidation(profile.phones.length ? profile.phones.map(phoneToPhoneValidation) : [defaultPhone])
    })
  })

  on(actions.updateFirstName, (state, value) => {
    return copy(state, {
      firstName: updateFieldValidation(state.firstName, value, validateNonBlank(value))
    })
  })

  on(actions.updateLastName, (state, value) => {
    return copy(state, {
      lastName: updateFieldValidation(state.lastName, value, validateNonBlank(value))
    })
  })

  on(actions.updateAddress, (state, value) => {
    return copy(state, {
      address: updateFieldValidation(state.address, value, validateNonBlank(value))
    })
  })

  on(actions.updateZipCode, (state, value) => {
    return copy(state, {
      zipCode: updateFieldValidation(state.zipCode, value, validateZipCode(value))
    })
  })

  on(actions.updateCity, (state, value) => {
    return copy(state, {
      city: updateFieldValidation(state.city, value, validateNonBlank(value))
    })
  })

  on(actions.addPhone, (state) => {
    const newPhones = arrays.append(state.phones.value, defaultPhone)
    return copy(state, {
      phones: updateFieldValidation(state.phones, newPhones, validateNonEmptyArray(newPhones))
    })
  })

  on(actions.removePhone, (state, index) => {
    const newPhones = arrays.remove(state.phones.value, index)
    return copy(state, {
      phones: updateFieldValidation(state.phones, newPhones, validateNonEmptyArray(newPhones))
    })
  })

  on(actions.updatePhoneName, (state, {index, value}) => {
    const oldPhone = state.phones.value[index]
    const newPhone = copy(oldPhone, {
      name: updateFieldValidation(oldPhone.name, value, validateNonBlank(value))
    })
    const newPhones = arrays.replace(state.phones.value, index, newPhone)
    return copy(state, {
      phones: updateFieldValidation(state.phones, newPhones, validateNonEmptyArray(newPhones))
    })
  })

  on(actions.updatePhoneNumber, (state, {index, value}) => {
    const oldPhone = state.phones.value[index]
    const newPhone = copy(oldPhone, {
      number: updateFieldValidation(oldPhone.number, value, validateNonBlank(value))
    })
    const newPhones = arrays.replace(state.phones.value, index, newPhone)
    return copy(state, {
      phones: updateFieldValidation(state.phones, newPhones, validateNonEmptyArray(newPhones))
    })
  })

  on(actions.closeErrors, (state) => {
    return copy(state, {errors: null})
  })

  on(actions.success, (state) => {
    return copy(state, {step: "success"})
  })
})

function phoneToPhoneValidation(phone: Phone): PhoneValidation {
  return {
    name: defaultFieldValidation(phone.name),
    number: defaultFieldValidation(phone.number)
  }
}

function validateZipCode(value: string): Validation<string> {
  return Valid(value)
    .filter(_ => /^[0-9]{5}$/.test(_), "Veuillez indiquer un code postal valide sur cinq chiffres")
}