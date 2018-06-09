import preact from "preact"
import Switch from '../../../framework/components/form/Switch'
import {ReservationFormState, reservationFormStore} from "./reservationFormStore";
import * as actions from "./reservationFormActions"
import classnames from "classnames"
import {Company} from "../../company/companyModel";
import StoreListenerComponent from "../../../framework/utils/dom";
import * as arrays from "../../../framework/utils/arrays";
import {createReservation} from "../reservationApi";
import Form from "../../../framework/components/form/Form";
import {Seat} from "../../theater/theaterModel";
import Input, {InputNumber} from "../../../framework/components/form/Input";
import {ReservationCreation} from "../reservationModel";
import Panel from "../../../framework/components/Panel";
import {isBeforeNow} from "../../../framework/utils/dates";
import {reservationDone} from "../reservationActions";
import {Show} from "../../show/showModel";
import {Play} from "../../play/playModel";
import PanelError from "../../../framework/components/form/PanelError";

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
    actions.initialize(this.props.play)
  }

  render(props: ReservationFormProps, state: ReservationFormState) {
    if (this.mounted && state.reservedSeats) {
      if (this.isReservationClosed(props)) {
        return this.renderClosedReservation(props, state)
      } else {
        return this.renderForm(props, state)
      }
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
        showErrorBottom
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
        {this.renderPrices(props, state)}
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
      <div class={classnames("plan", {"disabled": this.isReservationClosed(props)})}>
        <div class="canvas" style={{
          width: `${maxX + minX}px`,
          height: `${maxY + minY}px`
        }}>
          {seats.map((seat, index) => this.renderSeat(props, state, seat, index))}
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

  renderSeat(props: ReservationFormProps, state: ReservationFormState, seat: Seat, index: number) {
    const classNames = classnames("seat", seat.t, {
      taken: arrays.contains(state.reservedSeats, seat.c),
      selected: arrays.contains(state.seats.value, seat.c)
    })
    const action = this.isReservationClosed(props) || seat.t === "stage"
      ? () => {}
      : () => actions.toggleSeat(seat.c);
    return (
      <div key={index.toString()}
           class={classNames}
           style={{
             top: `${seat.y}px`,
             left: `${seat.x}px`,
             width: `${seat.w}px`,
             height: `${seat.h}px`
           }}
           onClick={action}
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

        <Switch
          id="newsletter"
          label="Je souhaite recevoir la newsletter de Voir & Entendre (vous pourrez vous désinscrire à tout moment)"
          name="newsletter"
          placeholderOff="Non"
          placeholderOn="Oui"
          onUpdate={actions.updateNewsletter}
          fieldValidation={state.newsletter}
        />
      </div>
    )
  }

  renderPrices(props: ReservationFormProps, state: ReservationFormState) {
    return (
      <div>
        <h2>Répartition par tarif</h2>
        <div class="row">
          {state.prices.value.map(price => (
            <div class="col-1">
              <InputNumber
                id="comment"
                label={`${price.price.name} (${price.price.value}€)`}
                name="comment"
                min={0}
                max={state.seats.value.length}
                placeholder={price.price.condition}
                onUpdate={count => actions.updatePrice({price: price.price.name, value: count})}
                fieldValidation={price.count}
              />
            </div>
          ))}
        </div>
        <PanelError fieldValidation={state.prices} />
      </div>
    )
  }

  renderClosedReservation(props: ReservationFormProps, state: ReservationFormState) {
    return (
      <div>
        <Panel type="info">
          <p>
            Les réservations sont closes.
          </p>
          <p>
            Cependant, s'il reste des places, n'hésitez pas à vous présenter à la caisse où vous pourrez en acheter.
            Préférez vous présenter un quart d'heure voire une demi-heure en avance
            afin de vous garantir au mieux l'obtention des places.
          </p>
        </Panel>
        {this.renderPlan(props, state)}
      </div>
    )
  }

  isReservationClosed(props: ReservationFormProps) {
    return isBeforeNow(props.play.reservationEndDate)
  }

  onSubmit = () => {
    const normalizedReservation: ReservationCreation = {
      firstName: this.state.firstName.value,
      lastName: this.state.lastName.value,
      email: this.state.email.value,
      city: this.state.city.value,
      comment: this.state.comment.value,
      newsletter: this.state.newsletter.value,
      seats: this.state.seats.value,
      prices: this.state.prices.value.map(_ => ({price: _.price.name, count: _.count.value}))
    }
    createReservation(this.props.play.id, normalizedReservation)
      .then(_ => {
        actions.success()
        reservationDone()
      })
      .catch(actions.updateErrors)
  }
}