import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import StoreListenerComponent from "../../../framework/utils/dom"
import {TheaterCreationState, theaterCreationStore} from "./theaterCreationStore";
import * as actions from "./theaterCreationActions";
import {createTheater} from "../theaterApi";
import {TheaterPlanEdition} from "./theaterPlanEdition";
import {PrimaryButton} from "../../../framework/components/buttons";

export interface TheaterCreationProps {
  path: string
}

export default class TheaterCreation extends StoreListenerComponent<TheaterCreationProps, TheaterCreationState> {
  constructor() {
    super(theaterCreationStore())
  }

  render(props: TheaterCreationProps, state: TheaterCreationState) {
    if (this.mounted) {
      return (
        <Page title="Créer un nouveau théâtre" role="admin">
          {
            state.step === "form"
              ? this.renderForm(state)
              : this.renderSuccess()
          }
        </Page>
      )
    } else {
      return null
    }
  }

  renderForm(state: TheaterCreationState) {
    return (
      <Form
        submit="Créer le théâtre"
        onSubmit={this.onSubmit}
        cancel="Revenir à l'accueil"
        onCancel="/"
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Input
          id="name"
          label="Nom du théâtre"
          name="name"
          type="text"
          placeholder="Nom du théâtre, tel qu'il sera affiché sur le site"
          required
          onUpdate={actions.updateName}
          fieldValidation={state.name}
        />

        <Input
          id="address"
          label="Adresse"
          name="address"
          type="text"
          placeholder="Adresse du théâtre: numéro rue, ville"
          required
          onUpdate={actions.updateAddress}
          fieldValidation={state.address}
        />

        <Input
          id="content"
          label="Description"
          name="content"
          type="textarea"
          placeholder="Description du théâtre, informations complémentaires"
          required
          onUpdate={actions.updateContent}
          fieldValidation={state.content}
        />

        <TheaterPlanEdition
          seats={state.seats}
          selectedSeatIndex={state.selectedSeat}
        />
      </Form>
    )
  }

  renderSuccess() {
    return (
      <Panel type="success">
        <p>
          Le théâtre a bien été créé.
        </p>
        <p>
          <PrimaryButton message="Revenir à l'accueil" href="/" />
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    createTheater({
      name: this.state.name.value,
      address: this.state.address.value,
      content: this.state.content.value,
      seats: this.state.seats.value.map(_ => ({
        c: _.c.value,
        x: _.x.value,
        y: _.y.value,
        w: _.w.value,
        h: _.h.value,
        t: _.t.value
      }))
    })
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}