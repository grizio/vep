import preact from "preact"
import {FieldValidation} from "../../utils/Validation"

interface InputProps {
  id: string
  label: string
  name: string
  type?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  onUpdate(value: string): void
  fieldValidation: FieldValidation<string>
}

interface InputNumberProps {
  id: string
  label: string
  name: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  onUpdate(value: number): void
  fieldValidation: FieldValidation<number>
}

export default function Input(props: InputProps) {
  const label = props.required ? `${props.label} *` : props.label
  const className = errorIsShown(props.fieldValidation)
    ? "error"
    : successIsShown(props.fieldValidation) ? "success" : ""
  return (
    <div class="field">
      {
        props.type === "textarea"
          ? renderTextarea(props, className)
          : renderInput(props, className)
      }
      <label for={props.id}>{label}</label>
      {renderError(props.fieldValidation)}
    </div>
  )
}

export function InputNumber(props: InputNumberProps) {
  return (
    <Input
      id={props.id}
      label={props.label}
      name={props.name}
      type="number"
      placeholder={props.placeholder}
      required={props.required}
      disabled={props.disabled}
      onUpdate={props.onUpdate ? (n) => props.onUpdate(parseInt(n, 10)) : null}
      fieldValidation={{
        value: props.fieldValidation.value.toString(),
        errors: props.fieldValidation.errors,
        changed: props.fieldValidation.changed
      }}
    />
  )
}

function renderInput(props: InputProps, className: string) {
  return (
    <input
      type={props.type || "text"}
      name={props.name}
      id={props.id}
      placeholder={props.placeholder || props.label}
      required={props.required}
      disabled={props.disabled}
      onInput={e => props.onUpdate((e.target as HTMLInputElement).value)}
      value={props.fieldValidation ? props.fieldValidation.value || "" : ""}
      class={className}
    />
  )
}

function renderTextarea(props: InputProps, className: string) {
  return (
    <textarea
      style={{height: `${props.fieldValidation.value.split("\n").length * 22 + 40}px`}}
      name={props.name}
      id={props.id}
      placeholder={props.placeholder || props.label}
      required={props.required}
      disabled={props.disabled}
      onInput={e => props.onUpdate((e.target as HTMLInputElement).value)}
      class={className}
      value={props.fieldValidation ? props.fieldValidation.value || "" : ""}
    />
  )
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