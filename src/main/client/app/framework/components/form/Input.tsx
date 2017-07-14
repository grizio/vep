import preact from "preact"
import {FieldValidation} from "../../utils/Validation"

interface InputProps {
  id: string
  label: string
  name: string
  type?: string
  placeholder?: string
  required?: boolean
  onUpdate(value: string): void
  fieldValidation: FieldValidation<string>
}

export default function Input(props: InputProps) {
  const label = props.required ? `${props.label} *` : props.label
  const className = errorIsShown(props.fieldValidation)
    ? "error"
    : successIsShown(props.fieldValidation) ? "success" : ""
  return (
    <div class="field">
      <input
        type={props.type || "text"}
        name={props.name}
        id={props.id}
        placeholder={props.placeholder || props.label}
        required={props.required}
        onInput={e => props.onUpdate((e.target as HTMLInputElement).value)}
        value={props.fieldValidation ? props.fieldValidation.value || "" : ""}
        class={className}
      />
      <label for={props.id}>{label}</label>
      {showError(props.fieldValidation)}
    </div>
  )
}

function showError(fieldValidation: FieldValidation<string>) {
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