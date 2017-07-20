import preact from "preact"
import {Seat, SeatType} from "../theaterModel"
import {allEmpty, isNotEmpty, max, min} from "../../../framework/utils/arrays"
import {PrimaryButton, SecondaryButton} from "../../../framework/components/buttons"
import * as actions from "./theaterFormActions"
import Input, {InputNumber} from "../../../framework/components/form/Input";
import Select from "../../../framework/components/form/Select";
import {defaultFieldValidation, FieldValidation} from "../../../framework/utils/Validation";
import {SeatValidation} from "./theaterFormStore";
import {nullOrUndefined} from "../../../framework/utils/object";
import classnames from "classnames";
import Panel from "../../../framework/components/Panel";

export interface TheaterPlanEditionProps {
  seats: FieldValidation<Array<SeatValidation>>
  selectedSeatIndex?: number
}

interface SeatInfo extends Seat {
  valid: boolean
}

const seatTypeOptions = [
  {value: "normal", label: "Normal"},
  {value: "strap", label: "Strapontin"},
  {value: "stage", label: "Scène"}
]

const emptySeat = {
  c: defaultFieldValidation(""),
  x: defaultFieldValidation(0),
  y: defaultFieldValidation(0),
  w: defaultFieldValidation(0),
  h: defaultFieldValidation(0),
  t: defaultFieldValidation("" as SeatType)
}

export function TheaterPlanEdition(props: TheaterPlanEditionProps) {
  return (
    <div class="plan editing">
      <div class="row">
        <div class="col-fill">
          {renderMainError(props)}
          {renderPlan(props)}
        </div>
        <div class="col">
          <PrimaryButton message="Ajouter un siège" action={actions.addSeat}/>
          {renderSeatInformation(props)}
        </div>
      </div>
    </div>
  )
}

function renderMainError(props: TheaterPlanEditionProps) {
  if (isNotEmpty(props.seats.errors)) {
    return (
      <Panel type="error">
        {props.seats.errors.map(error => <p>{error}</p>)}
      </Panel>
    )
  } else {
    return null
  }
}

function renderPlan(props: TheaterPlanEditionProps) {
  const seats = flattenSeats(props.seats.value)
  // minX and minY are for same margins vertically and horizontally
  const minX = min(seats.map(_ => _.x))
  const maxX = max(seats.map(_ => _.x + _.w))
  const minY = min(seats.map(_ => _.y))
  const maxY = max(seats.map(_ => _.y + _.h))
  return (
    <div class="canvas" style={{
      width: `${maxX + minX}px`,
      height: `${maxY + minY}px`
    }}>
      { seats.map((seat, index) => renderSeat(props, seat, index)) }
    </div>
  )
}

function renderSeat(props: TheaterPlanEditionProps, seat: SeatInfo, index: number) {
  return (
    <div key={index.toString()}
         class={classnames("seat", seat.t, {invalid: !seat.valid, selected: index === props.selectedSeatIndex})}
         style={{
           top: `${seat.y}px`,
           left: `${seat.x}px`,
           width: `${seat.w}px`,
           height: `${seat.h}px`
         }}
         onClick={() => actions.selectSeat(index)}
    >
      {seat.c}
    </div>
  )
}

function renderSeatInformation(props: TheaterPlanEditionProps) {
  const disabled = nullOrUndefined(props.selectedSeatIndex)
  const seat = disabled ? emptySeat : props.seats.value[props.selectedSeatIndex];
  return (
    <div>
      <Input
        id={`seat-${props.selectedSeatIndex}-name`}
        label="Nom"
        name={`seat-${props.selectedSeatIndex}-name`}
        type="text"
        placeholder="Ex: A1, B12"
        required
        disabled={disabled}
        onUpdate={(value) => actions.updateSeatCode({index: props.selectedSeatIndex, value})}
        fieldValidation={seat.c}
      />

      <InputNumber
        id={`seat-${props.selectedSeatIndex}-x`}
        label="Abscisse"
        name={`seat-${props.selectedSeatIndex}-x`}
        placeholder="Position depuis la gauche"
        required
        disabled={disabled}
        onUpdate={(value) => actions.updateSeatX({index: props.selectedSeatIndex, value})}
        fieldValidation={seat.x}
      />

      <InputNumber
        id={`seat-${props.selectedSeatIndex}-y`}
        label="Ordonné"
        name={`seat-${props.selectedSeatIndex}-y`}
        placeholder="Position depuis le haut"
        required
        disabled={disabled}
        onUpdate={(value) => actions.updateSeatY({index: props.selectedSeatIndex, value})}
        fieldValidation={seat.y}
      />

      <InputNumber
        id={`seat-${props.selectedSeatIndex}-width`}
        label="Largeur"
        name={`seat-${props.selectedSeatIndex}-width`}
        placeholder="Largeur du siège"
        required
        disabled={disabled}
        onUpdate={(value) => actions.updateSeatWidth({index: props.selectedSeatIndex, value})}
        fieldValidation={seat.w}
      />

      <InputNumber
        id={`seat-${props.selectedSeatIndex}-height`}
        label="Hauteur"
        name={`seat-${props.selectedSeatIndex}-hauteur`}
        placeholder="Hauteur du siège"
        required
        disabled={disabled}
        onUpdate={(value) => actions.updateSeatHeight({index: props.selectedSeatIndex, value})}
        fieldValidation={seat.h}
      />

      <Select
        id={`seat-${props.selectedSeatIndex}-type`}
        label="Type"
        name={`seat-${props.selectedSeatIndex}-type`}
        placeholder="Type de siège"
        required
        disabled={disabled}
        options={seatTypeOptions}
        onUpdate={(value) => actions.updateSeatType({index: props.selectedSeatIndex, value})}
        fieldValidation={seat.t}
      />

      <SecondaryButton message="Supprimer le siège"
                       action={() => actions.removeSeat(props.selectedSeatIndex)}
                       disabled={disabled}
      />
    </div>
  )
}

function flattenSeats(seats: Array<SeatValidation>): Array<SeatInfo> {
  return seats.map(seat => ({
    c: seat.c.value,
    x: seat.x.value,
    y: seat.y.value,
    w: seat.w.value,
    h: seat.h.value,
    t: seat.t.value,
    valid: allEmpty(seat.c.errors, seat.x.errors, seat.y.errors, seat.w.errors, seat.h.errors, seat.t.errors)
  }))
}