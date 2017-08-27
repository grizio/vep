import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import StoreListenerComponent from "../../../framework/utils/dom"
import {TheaterFormState, theaterFormStore} from "./theaterFormStore";
import * as actions from "./theaterFormActions";
import {createTheater, findTheater, updateTheater} from "../theaterApi";
import {TheaterPlanEdition} from "./theaterPlanEdition";
import {PrimaryButton} from "../../../framework/components/buttons";
import {Theater, TheaterCreation} from "../theaterModel";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import RichInput from "../../../framework/components/form/RichInput";

export interface TheaterFormProps {
  id?: string
}

export default class TheaterForm extends StoreListenerComponent<TheaterFormProps, TheaterFormState> {
  constructor() {
    super(theaterFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    if (this.props.id) {
      findTheater(this.props.id).then(actions.initialize)
    } else {
      actions.initializeEmpty()
    }
  }

  render(props: TheaterFormProps, state: TheaterFormState) {
    if (this.mounted) {
      return (
        <Page title={props.id ? "Modifier un théâtre" : "Créer un nouveau théatre"} role="admin">
          <Loading loading={state.step === "loading"} message={messages.production.theater.form.loading}>
            {
              state.step === "form"
                ? this.renderForm(props, state)
                : this.renderSuccess(props)
            }
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderForm(props: TheaterFormProps, state: TheaterFormState) {
    return (
      <Form
        submit={props.id ? "Modifier le théâtre" : "Créer le théâtre"}
        onSubmit={this.onSubmit}
        cancel="Revenir à la liste"
        onCancel="/production/theaters"
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

        <RichInput
          id="content"
          label="Description"
          name="content"
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

  renderSuccess(props: TheaterFormProps) {
    return (
      <Panel type="success">
        <p>
          {props.id ? "Le théâtre a bien été modifié." : "Le théâtre a bien été créé."}
        </p>
        <p>
          <PrimaryButton message="Revenir à la liste" href="/production/theaters" />
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedTheater: Theater | TheaterCreation = {
      id: this.state.id,
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
    }
    const action = normalizedTheater.id
      ? updateTheater(normalizedTheater)
      : createTheater(normalizedTheater)
    action
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}