import preact from "preact"
import {ReservationListState, reservationListStore} from "./reservationListStore";
import * as actions from "./reservationListActions"
import StoreListenerComponent from "../../../framework/utils/dom";
import {findReservations} from "../reservationApi";
import {Reservation} from "../reservationModel";
import {Play} from "../../company/companyModel";
import CardCollection from "../../../framework/components/card/CardCollection";
import {Card, CardAction, CardContent} from "../../../framework/components/card/Card";
import {OnGranted} from "../../../framework/components/Security";

export interface ReservationListProps {
  play: Play
}

class ReservationListComponent extends StoreListenerComponent<ReservationListProps, ReservationListState> {
  constructor() {
    super(reservationListStore())
  }

  componentDidMount() {
    super.componentDidMount()
    findReservations(this.props.play.id)
      .then(reservations => actions.initialize({reservations}))
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
        <h2>Réservations</h2>
        <CardCollection columns={4}>
          {state.reservations.map(this.renderReservation)}
        </CardCollection>
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
        <CardAction href={`mailto:${reservation.email}`}>Contacter par email</CardAction>
      </Card>
    )
  }
}

const ReservationList = OnGranted<ReservationListProps>(ReservationListComponent, "admin")
export default ReservationList