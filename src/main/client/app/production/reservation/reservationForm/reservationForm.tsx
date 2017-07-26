import preact from "preact"
import {ReservationFormState, reservationFormStore} from "./reservationFormStore";
import * as actions from "./reservationFormActions"
import classnames from "classnames"
import {Company, Play, Show} from "../../company/companyModel";
import StoreListenerComponent from "../../../framework/utils/dom";
import * as arrays from "../../../framework/utils/arrays";
import {createReservation, findReservedSeats} from "../reservationApi";
import Form from "../../../framework/components/form/Form";
import {Seat} from "../../theater/theaterModel";
import Input from "../../../framework/components/form/Input";
import {ReservationCreation} from "./reservationModel";
import Panel from "../../../framework/components/Panel";

export interface ReservationFormProps {
  company: Company
  show: Show
  play: Play
}

export default class ReservationForm extends StoreListenerComponent<ReservationFormProps, ReservationFormState> {
  constructor() {
    super(reservationFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    findReservedSeats(this.props.play.id)
      .then(reservedSeats => actions.initialize({reservedSeats}))
  }

  render(props: ReservationFormProps, state: ReservationFormState) {
    if (this.mounted && state.reservedSeats) {
      return this.renderForm(props, state)
    } else {
      return null
    }
  }

  renderForm(props: ReservationFormProps, state: ReservationFormState) {
    return (
      <Form
        submit={"Réserver"}
        onSubmit={this.onSubmit}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        {
          state.success
            ? (
              <Panel type="success" onClose={actions.closeSuccess}>
                <p>Vos places ont été réservées</p>
              </Panel>
            ) : null
        }
        <div class="row">
          <div class="col-2">
            {this.renderPlan(props, state)}
          </div>
          <div class="col-1">
            {this.renderPersonalInformation(state)}
          </div>
        </div>
      </Form>
    )
  }

  renderPlan(props: ReservationFormProps, state: ReservationFormState) {
    const seats = props.play.theater.seats
    // minX and minY are for same margins vertically and horizontally
    const minX = arrays.min(seats.map(_ => _.x))
    const maxX = arrays.max(seats.map(_ => _.x + _.w))
    const minY = arrays.min(seats.map(_ => _.y))
    const maxY = arrays.max(seats.map(_ => _.y + _.h))
    return (
      <div class="plan">
        <div class="canvas" style={{
          width: `${maxX + minX}px`,
          height: `${maxY + minY}px`
        }}>
          {seats.map((seat, index) => this.renderSeat(state, seat, index))}
        </div>
        {
          (state.seats.errors && state.seats.errors.length) ? (
            <Panel type="error">
              {state.seats.errors.map(_ => <p>{_}</p>)}
            </Panel>
          ) : null
        }
      </div>
    )
  }

  renderSeat(state: ReservationFormState, seat: Seat, index: number) {
    const classNames = classnames("seat", seat.t, {
      taken: arrays.contains(state.reservedSeats, seat.c),
      selected: arrays.contains(state.seats.value, seat.c)
    })
    return (
      <div key={index.toString()}
           class={classNames}
           style={{
             top: `${seat.y}px`,
             left: `${seat.x}px`,
             width: `${seat.w}px`,
             height: `${seat.h}px`
           }}
           onClick={() => actions.toggleSeat(seat.c)}
      >
        {seat.c}
      </div>
    )
  }

  renderPersonalInformation(state: ReservationFormState) {
    return (
      <div>
        <Input
          id="firstName"
          label="Prénom"
          name="firstName"
          type="text"
          placeholder="Votre prénom"
          required
          onUpdate={actions.updateFirstName}
          fieldValidation={state.firstName}
        />

        <Input
          id="lastName"
          label="Nom"
          name="lastName"
          type="text"
          placeholder="Votre nom de famillle"
          required
          onUpdate={actions.updateLastName}
          fieldValidation={state.lastName}
        />

        <Input
          id="email"
          label="Adresse e-mail"
          name="email"
          type="email"
          placeholder="Votre adresse e-mail valide"
          required
          onUpdate={actions.updateEmail}
          fieldValidation={state.email}
        />

        <Input
          id="city"
          label="Ville"
          name="director"
          type="text"
          placeholder="Votre ville"
          onUpdate={actions.updateCity}
          fieldValidation={state.city}
        />

        <Input
          id="comment"
          label="Commentaire"
          name="comment"
          type="textarea"
          placeholder="Si vous souhaitez ajouter des informations supplémentaires"
          onUpdate={actions.updateComment}
          fieldValidation={state.comment}
        />
      </div>
    )
  }

  onSubmit = () => {
    const normalizedReseration: ReservationCreation = {
      firstName: this.state.firstName.value,
      lastName: this.state.lastName.value,
      email: this.state.email.value,
      city: this.state.city.value,
      comment: this.state.comment.value,
      seats: this.state.seats.value
    }
    createReservation(this.props.play.id, normalizedReseration)
      .then(actions.success)
      .catch(actions.updateErrors)
  }
}