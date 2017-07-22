import preact from "preact"
import {FieldValidation} from "../../utils/Validation"

interface SwitchProps {
  id: string
  label: string
  placeholderOn?: string
  placeholderOff?: string
  name: string
  required?: boolean
  disabled?: boolean

  onUpdate(value: boolean): void

  fieldValidation: FieldValidation<boolean>
}

export default function Switch(props: SwitchProps) {
  const label = props.required ? `${props.label} *` : props.label
  return (
    <div class="field">
      {renderSwitch(props)}
      <label for={props.id}>{label}</label>
      {renderError(props.fieldValidation)}
    </div>
  )
}

function renderSwitch(props: SwitchProps) {
  const className = errorIsShown(props.fieldValidation)
    ? "error"
    : successIsShown(props.fieldValidation) ? "success" : ""
  const placeholderOn = props.placeholderOn || ""
  const placeholderOff = props.placeholderOff || ""
  return (
    <label class="switch">
      <input
        type="checkbox"
        id={props.id}
        name={props.name}
        required={props.required}
        disabled={props.disabled}
        onChange={e => props.onUpdate((e.target as HTMLInputElement).checked)}
        checked={props.fieldValidation ? props.fieldValidation.value || false : false}
        class={className}
      />
      <span class="lever" />
      <span class="left-placeholder">{placeholderOff}</span>
      <span class="right-placeholder">{placeholderOn}</span>
    </label>
  )
}

function renderError(fieldValidation: FieldValidation<boolean>) {
  if (errorIsShown(fieldValidation)) {
    return fieldValidation.errors.map((error, i) =>
      <span class="error-message" key={i.toString()}>{fieldValidation.errors}</span>
    )
  } else {
    // Force the component having an height by not being empty.
    return <span class="error-message none">&nbsp;</span>
  }
}

function errorIsShown(fieldValidation: FieldValidation<boolean>) {
  return fieldValidation && fieldValidation.errors && fieldValidation.errors.length && fieldValidation.changed
}

function successIsShown(fieldValidation: FieldValidation<boolean>) {
  return fieldValidation && (!fieldValidation.errors || !fieldValidation.errors.length) && fieldValidation.changed
}