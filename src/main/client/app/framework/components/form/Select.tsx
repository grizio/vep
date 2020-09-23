import preact from "preact"
import {FieldValidation} from "../../utils/Validation"
import {contains} from "../../utils/arrays";

interface SelectProps {
  id: string
  label: string
  name: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  options: Array<SelectOption>
  onUpdate(value: string): void
  fieldValidation: FieldValidation<string>
}

interface SelectOption {
  value: string
  label: string
}

export default function Select(props: SelectProps) {
  const label = props.required ? `${props.label} *` : props.label
  const className = errorIsShown(props.fieldValidation)
    ? "error"
    : successIsShown(props.fieldValidation) ? "success" : ""
  return (
    <div class="field">
      {renderSelect(props, className)}
      <label for={props.id}>{label}</label>
      {renderError(props.fieldValidation)}
    </div>
  )
}

function renderSelect(props: SelectProps, className: string) {
  return (
    <select
      name={props.name}
      id={props.id}
      required={props.required}
      disabled={props.disabled}
      onChange={e => props.onUpdate((e.target as HTMLInputElement).value)}
      class={className}
    >
      {renderPlaceholder(props)}
      {
        props.options.map(option =>
          <option value={option.value} selected={props.fieldValidation.value === option.value}>{option.label}</option>
        )
      }
    </select>
  )
}

function renderPlaceholder(props: SelectProps) {
  const isValid = isValueValid(props)
  if (props.placeholder) {
    if (props.required) {
      return <option value={null} disabled selected={!isValid} hidden class="option-placeholder">{props.placeholder}</option>
    } else {
      return <option value={null} selected={!isValid} class="option-placeholder">{props.placeholder}</option>
    }
  } else {
    return null
  }
}

function isValueValid(props: SelectProps) {
  const value = props.fieldValidation.value
  if (value === null || value === undefined) {
    return false
  } else {
    const options = props.options.map(_ => _.value)
    return contains(options, value)
  }
}

function renderError(fieldValidation: FieldValidation<string>) {
  if (errorIsShown(fieldValidation)) {
    return fieldValidation.errors.map((error, i) =>
      <span class="error-message" key={i.toString()}>{fieldValidation.errors}</span>
    )
  } else {
    // Force the component having an height by not being empty.
    return <span class="error-message none">&nbsp;</span>
  }
}

function errorIsShown(fieldValidation: FieldValidation<string>) {
  return fieldValidation && fieldValidation.errors && fieldValidation.errors.length && fieldValidation.changed
}

function successIsShown(fieldValidation: FieldValidation<string>) {
  return fieldValidation && (!fieldValidation.errors || !fieldValidation.errors.length) && fieldValidation.changed
}