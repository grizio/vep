import preact from "preact"
import {FieldValidation} from "../../utils/Validation"
import * as compatibility from "../../utils/compatibility";
import {
  localIsoFormat, localIsoFormatToDate,
  shortDateFormat, shortDateFormatToDate, shortDateTimeFormat,
  shortDateTimeFormatToDate, timeFormat, timeFormatToDate
} from "../../utils/dates";

interface InputDateFieldProps {
  id: string
  label: string
  name: string
  required?: boolean
  disabled?: boolean
  fieldValidation: FieldValidation<Date>

  onUpdate(value: Date): void
}

interface InputDateProps extends InputDateFieldProps {
}

interface InputDateTimeProps extends InputDateFieldProps {
}

interface InputTimeProps extends InputDateFieldProps {
}

interface InputFieldProps {
  type: "date" | "datetime-local" | "time"
  id: string
  label: string
  name: string
  placeholder: string
  required?: boolean
  disabled?: boolean
  fieldValidation: FieldValidation<Date>

  onUpdate(value: Date): void

  normalizeToDate(value: string): Date

  normalizeFromDate(value: Date): string
}

interface InputFieldState {
  value: string
  focus: boolean
}

export function InputDate(props: InputDateProps) {
  return (
    <InputDateField
      type="date"
      id={props.id}
      label={props.label}
      name={props.name}
      placeholder={compatibility.acceptInputDate ? "" : "jj/mm/aaaa"}
      required={props.required}
      disabled={props.disabled}
      fieldValidation={props.fieldValidation}
      onUpdate={props.onUpdate}
      normalizeToDate={(text) => compatibility.acceptInputDate ? localIsoFormatToDate(text) : shortDateFormatToDate(text)}
      normalizeFromDate={(date) => compatibility.acceptInputDate ? localIsoFormat(date) : shortDateFormat(date)}
    />
  )
}

export function InputDateTime(props: InputDateTimeProps) {
  return (
    <InputDateField
      type="datetime-local"
      id={props.id}
      label={props.label}
      name={props.name}
      placeholder={compatibility.acceptInputDate ? "" : "jj/mm/aaaa hh:mm"}
      required={props.required}
      disabled={props.disabled}
      fieldValidation={props.fieldValidation}
      onUpdate={props.onUpdate}
      normalizeToDate={(text) => compatibility.acceptInputDateTimeLocal ? localIsoFormatToDate(text) : shortDateTimeFormatToDate(text)}
      normalizeFromDate={(date) => compatibility.acceptInputDateTimeLocal ? localIsoFormat(date) : shortDateTimeFormat(date)}
    />
  )
}

export function InputTime(props: InputTimeProps) {
  return (
    <InputDateField
      type="time"
      id={props.id}
      label={props.label}
      name={props.name}
      placeholder={compatibility.acceptInputDate ? "" : "hh:mm"}
      required={props.required}
      disabled={props.disabled}
      fieldValidation={props.fieldValidation}
      onUpdate={props.onUpdate}
      normalizeToDate={(text) => compatibility.acceptInputTime ? localIsoFormatToDate(text) : timeFormatToDate(text)}
      normalizeFromDate={(date) => compatibility.acceptInputTime ? localIsoFormat(date) : timeFormat(date)}
    />
  )
}

class InputDateField extends preact.Component<InputFieldProps, InputFieldState> {
  componentWillReceiveProps(nextProps: InputFieldProps) {
    if (nextProps.fieldValidation) {
      const newValue = this.props.normalizeFromDate(nextProps.fieldValidation.value)
      if (newValue !== this.state.value) {
        this.setState({value: newValue})
      }
    }
  }

  shouldComponentUpdate(nextProps: InputFieldProps, nextState: InputFieldState) {
    const nextPropsValue = this.props.normalizeFromDate(nextProps.fieldValidation.value);
    return nextProps.type !== this.props.type ||
      nextProps.id !== this.props.id ||
      nextProps.label !== this.props.label ||
      nextProps.name !== this.props.name ||
      nextProps.placeholder !== this.props.placeholder ||
      nextProps.required !== this.props.required ||
      nextProps.disabled !== this.props.disabled ||
      nextPropsValue !== this.state.value && nextPropsValue !== nextState.value ||
      nextState.focus !== this.state.focus
  }

  render(props: InputFieldProps, state: InputFieldState) {
    const label = props.required ? `${props.label} *` : props.label
    const className = errorIsShown(props.fieldValidation)
      ? "error"
      : successIsShown(props.fieldValidation) ? "success" : ""
    return (
      <div class="field">
        <input
          type={props.type}
          name={props.name}
          id={props.id}
          placeholder={props.placeholder}
          required={props.required}
          disabled={props.disabled}
          onInput={this.onUpdate}
          value={state.value}
          class={className}
          onFocus={() => this.setState({focus: true})}
          onBlur={() => this.setState({focus: false})}
        />
        <label for={props.id}>{label}</label>
        {renderError(props.fieldValidation)}
      </div>
    )
  }

  onUpdate = (e: Event) => {
    const value = (e.target as HTMLInputElement).value
    this.setState({value})
    this.props.onUpdate(this.props.normalizeToDate(value))
  }
}

function renderError(fieldValidation: FieldValidation<Date>) {
  if (errorIsShown(fieldValidation)) {
    return fieldValidation.errors.map((error, i) =>
      <span class="error-message" key={i.toString()}>{fieldValidation.errors}</span>
    )
  } else {
    // Force the component having an height by not being empty.
    return <span class="error-message none">&nbsp;</span>
  }
}

function errorIsShown(fieldValidation: FieldValidation<Date>) {
  return fieldValidation && fieldValidation.errors && fieldValidation.errors.length && fieldValidation.changed
}

function successIsShown(fieldValidation: FieldValidation<Date>) {
  return fieldValidation && (!fieldValidation.errors || !fieldValidation.errors.length) && fieldValidation.changed
}