import preact from "preact"
import { FieldValidation } from "../../utils/Validation"
import * as compatibility from "../../utils/compatibility"
import {
  localDateIsoFormat,
  localDateTimeIsoFormat,
  localIsoFormatToDate,
  shortDateFormat,
  shortDateFormatToDate,
  timeFormat,
  timeFormatToDate
} from "../../utils/dates"

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
  mounted: boolean
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
      normalizeFromDate={(date) => compatibility.acceptInputDate ? localDateIsoFormat(date) : shortDateFormat(date)}
    />
  )
}

interface InputDateTimeState {
  mounted: boolean
  datePart: string
  timePart: string
  focus: boolean
}

export class InputDateTime extends preact.Component<InputDateTimeProps, InputDateTimeState> {
  constructor() {
    super()
    this.setState({ mounted: false })
  }

  componentDidMount() {
    if (this.props.fieldValidation.value !== null) {
      const datePart = localDateIsoFormat(this.props.fieldValidation.value)
      const timePart = timeFormat(this.props.fieldValidation.value)
      this.setState({ datePart, timePart, mounted: true })
    } else {
      this.setState({ datePart: null, timePart: null, mounted: true })
    }
  }

  componentWillReceiveProps(nextProps: InputFieldProps) {
    if (nextProps.fieldValidation && nextProps.fieldValidation.value) {
      const datePart = localDateIsoFormat(nextProps.fieldValidation.value)
      const timePart = timeFormat(nextProps.fieldValidation.value)
      if (datePart !== this.state.datePart || timePart !== this.state.timePart) {
        this.setState({ datePart, timePart })
      }
    }
  }

  shouldComponentUpdate(nextProps: InputDateTimeProps, nextState: InputDateTimeState) {
    const datePart = this.props.fieldValidation.value !== null ? localDateIsoFormat(this.props.fieldValidation.value) : null
    const timePart = this.props.fieldValidation.value !== null ? timeFormat(this.props.fieldValidation.value) : null
    return nextProps.id !== this.props.id ||
      nextProps.label !== this.props.label ||
      nextProps.name !== this.props.name ||
      nextProps.required !== this.props.required ||
      nextProps.disabled !== this.props.disabled ||
      datePart !== this.state.datePart && datePart !== nextState.datePart ||
      timePart !== this.state.timePart && timePart !== nextState.timePart ||
      nextState.focus !== this.state.focus ||
      nextState.mounted !== this.state.mounted
  }

  render(props: InputDateTimeProps, state: InputDateTimeState) {
    const label = props.required ? `${props.label} *` : props.label
    const className = errorIsShown(props.fieldValidation)
      ? "error"
      : successIsShown(props.fieldValidation) ? "success" : ""
    return (
      <div class="field">
        <div class="datetime">
          <input
            type="date"
            name={`${props.name}-date`}
            id={`${props.id}-date`}
            required={props.required}
            disabled={props.disabled}
            onInput={this.onUpdateDate}
            value={state.datePart}
            class={className}
            onFocus={() => this.setState({ focus: true })}
            onBlur={() => this.setState({ focus: false })}
          />
          <input
            type="time"
            name={`${props.name}-time`}
            id={`${props.id}-time`}
            required={props.required}
            disabled={props.disabled}
            onInput={this.onUpdateTime}
            value={state.timePart}
            class={className}
            onFocus={() => this.setState({ focus: true })}
            onBlur={() => this.setState({ focus: false })}
          />
        </div>
        <label for={props.id}>{label}</label>
        {renderError(props.fieldValidation)}
      </div>
    )
  }

  onUpdateDate = (e: Event) => {
    const value = (e.target as HTMLInputElement).value
    this.setState({ datePart: value })
    this.onUpdate()
  }

  onUpdateTime = (e: Event) => {
    const value = (e.target as HTMLInputElement).value
    this.setState({ timePart: value })
    this.onUpdate()
  }

  onUpdate = () => {
    if (this.state.datePart !== null && this.state.timePart !== null) {
      this.props.onUpdate(new Date(Date.parse(`${this.state.datePart}T${this.state.timePart}Z`)))
    }
  }
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
      normalizeFromDate={(date) => compatibility.acceptInputTime ? localDateTimeIsoFormat(date) : timeFormat(date)}
    />
  )
}

class InputDateField extends preact.Component<InputFieldProps, InputFieldState> {
  constructor() {
    super()
    this.setState({ mounted: false })
  }

  componentDidMount() {
    const value = this.props.normalizeFromDate(this.props.fieldValidation.value)
    this.setState({ value, mounted: true })
  }

  componentWillReceiveProps(nextProps: InputFieldProps) {
    if (nextProps.fieldValidation) {
      const newValue = this.props.normalizeFromDate(nextProps.fieldValidation.value)
      if (newValue !== this.state.value) {
        this.setState({ value: newValue })
      }
    }
  }

  shouldComponentUpdate(nextProps: InputFieldProps, nextState: InputFieldState) {
    const nextPropsValue = this.props.normalizeFromDate(nextProps.fieldValidation.value)
    return nextProps.type !== this.props.type ||
      nextProps.id !== this.props.id ||
      nextProps.label !== this.props.label ||
      nextProps.name !== this.props.name ||
      nextProps.placeholder !== this.props.placeholder ||
      nextProps.required !== this.props.required ||
      nextProps.disabled !== this.props.disabled ||
      nextPropsValue !== this.state.value && nextPropsValue !== nextState.value ||
      nextState.focus !== this.state.focus ||
      nextState.mounted !== this.state.mounted
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
          onFocus={() => this.setState({ focus: true })}
          onBlur={() => this.setState({ focus: false })}
        />
        <label for={props.id}>{label}</label>
        {renderError(props.fieldValidation)}
      </div>
    )
  }

  onUpdate = (e: Event) => {
    const value = (e.target as HTMLInputElement).value
    this.setState({ value })
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