import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import StoreListenerComponent from "../../../framework/utils/dom"
import {IconDeleteButton, PrimaryButton, SecondaryButton} from "../../../framework/components/buttons";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import {AdhesionMemberValidation, RequestAdhesionFormState, requestAdhesionFormStore} from "./requestAdhesionFormStore";
import * as actions from "./requestAdhesionFormActions"
import {InputDate} from "../../../framework/components/form/InputDate";
import {shortPeriodFormat} from "../../../common/types/Period";
import PanelError from "../../../framework/components/form/PanelError";
import {RequestAdhesion} from "../adhesionModel";
import {findOpenedRegistrationPeriods, requestAdhesion} from "../adhesionApi";
import Select from "../../../framework/components/form/Select";

export interface RequestAdhesionFormProps {
  id?: string
  path: string
}

export default class RequestAdhesionForm extends StoreListenerComponent<RequestAdhesionFormProps, RequestAdhesionFormState> {
  constructor() {
    super(requestAdhesionFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    findOpenedRegistrationPeriods()
      .then(acceptedPeriods => actions.initialize({acceptedPeriods}))
  }

  render(props: RequestAdhesionFormProps, state: RequestAdhesionFormState) {
    if (this.mounted) {
      return (
        <Page title={"(Ré)inscription aux activités"}
              role="admin">
          <Loading loading={state.step === "loading"} message={messages.user.adhesion.formRequest.loading}>
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

  renderForm(props: RequestAdhesionFormProps, state: RequestAdhesionFormState) {
    return (
      <Form
        submit="Envoyer la demande"
        onSubmit={this.onSubmit}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        {this.renderPeriod(state)}
        {this.renderMembers(state)}
      </Form>
    )
  }

  renderPeriod(state: RequestAdhesionFormState) {
    return (
      <Select
        id="period"
        label="Année"
        name="period"
        placeholder="Veuillez choisir"
        required
        options={state.acceptedPeriods.map(period => ({
          label: shortPeriodFormat(period.period),
          value: period.id
        }))}
        fieldValidation={state.period}
        onUpdate={actions.updatePeriod}
      />
    )
  }

  renderMembers(state: RequestAdhesionFormState) {
    return (
      <div>
        <h2>Participants</h2>

        <PanelError fieldValidation={state.members}/>

        {state.members.value.map((value, index) => this.renderMember(state, value, index))}

        <SecondaryButton message="Ajouter un participant" action={actions.addMember}/>
      </div>
    )
  }

  renderMember(state: RequestAdhesionFormState, member: AdhesionMemberValidation, index: number) {
    return (
      <div>
        <div class="row middle">
          <IconDeleteButton message="x" action={() => actions.removeMember(index)}/>
          <h3 class="col-fill">Participant {index + 1}</h3>
        </div>

        <Input
          id={`member-${index}-firstName`}
          label="Prénom"
          name={`member-${index}-firstName`}
          type="text"
          placeholder="Prénom du participant"
          required
          onUpdate={(value) => actions.updateMemberFirstName({index, value})}
          fieldValidation={member.firstName}
        />

        <Input
          id={`member-${index}-lastName`}
          label="Nom"
          name={`member-${index}-lastName`}
          type="text"
          placeholder="Nom de famille du participant"
          required
          onUpdate={(value) => actions.updateMemberLastName({index, value})}
          fieldValidation={member.lastName}
        />

        <InputDate
          id={`member-${index}-birthday`}
          label="Date de naissance"
          name={`member-${index}-birthday`}
          required
          onUpdate={(value) => actions.updateMemberBirthday({index, value})}
          fieldValidation={member.birthday}
        />

        <Select
          id={`member-${index}-activity`}
          label="Activité"
          name={`member-${index}-activity`}
          placeholder="Veuillez choisir"
          required
          options={this.getActivities(state)}
          onUpdate={(value) => actions.updateMemberActivity({index, value})}
          fieldValidation={member.activity}
        />
      </div>
    )
  }

  getActivities(state: RequestAdhesionFormState) {
    if (state.acceptedPeriods && state.period.value) {
      const period = state.acceptedPeriods.find(period => period.id === state.period.value)
      if (period) {
        return period.activities.map(activity => ({
          label: activity,
          value: activity
        }))
      } else {
        return []
      }
    } else {
      return []
    }
  }

  renderSuccess(props: RequestAdhesionFormProps) {
    return (
      <Panel type="success">
        <p>
          Votre demande à bien été envoyée.
          Elle sera validée dans les plus brefs délais.
        </p>
        <p>
          <PrimaryButton message="Revenir à la liste" href="/adhesions"/>
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedAdhesion: RequestAdhesion = {
      period: this.state.period.value,
      members: this.state.members.value.map(member => ({
        firstName: member.firstName.value,
        lastName: member.lastName.value,
        birthday: member.birthday.value,
        activity: member.activity.value
      }))
    }
    requestAdhesion(normalizedAdhesion)
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}