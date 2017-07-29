import preact from "preact"
import {ReservationListState, reservationListStore} from "./reservationListStore";
import * as actions from "./reservationListActions"
import StoreListenerComponent from "../../../framework/utils/dom";
import {deleteReservation} from "../reservationApi";
import {Reservation} from "../reservationModel";
import CardCollection from "../../../framework/components/card/CardCollection";
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import {OnGranted} from "../../../framework/components/Security";
import {reservationDeleted} from "../reservationActions";
import Panel from "../../../framework/components/Panel";
import {Play} from "../../play/playModel";

export interface ReservationListProps {
  play: Play
}

class ReservationListComponent extends StoreListenerComponent<ReservationListProps, ReservationListState> {
  constructor() {
    super(reservationListStore())
  }

  componentDidMount() {
    super.componentDidMount()
    actions.initialize(this.props.play.id)
  }

  render(props: ReservationListProps, state: ReservationListState) {
    if (this.mounted && state.reservations) {
      return this.renderReservations(state)
    } else {
      return null
    }
  }

  renderReservations(state: ReservationListState) {
    return (
      <div>
        <CardCollection columns={4}>
          {state.reservations.map(reservation => this.renderReservation(reservation))}
        </CardCollection>
        {state.reservations.length === 0 ? this.renderNone() : null}
      </div>
    )
  }

  renderReservation(reservation: Reservation) {
    return (
      <Card title={`${reservation.firstName} ${reservation.lastName}`}>
        <CardContent>
          <p>{reservation.seats.join(", ")}</p>
          {reservation.city ? <p>{reservation.city}</p> : null}
          {reservation.comment ? <p>{reservation.comment}</p> : null}
        </CardContent>
        <CardAction href={`mailto:${reservation.email}`}>📧</CardAction>
        <CardAction action={() => this.deleteReservation(reservation)}>Supprimer</CardAction>
      </Card>
    )
  }

  renderNone() {
    return (
      <Panel type="info">
        <p>Aucune réservation pour le moment.</p>
      </Panel>
    )
  }

  deleteReservation(reservation: Reservation) {
    deleteReservation(this.props.play.id, reservation)
      .then(_ => reservationDeleted())
  }
}

const ReservationList = OnGranted<ReservationListProps>(ReservationListComponent, "admin")
export default ReservationList