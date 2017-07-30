import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import StoreListenerComponent from "../../../framework/utils/dom"
import {IconDeleteButton, PrimaryButton, SecondaryButton} from "../../../framework/components/buttons";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import {PeriodAdhesionFormState, periodAdhesionFormStore} from "./periodAdhesionFormStore";
import * as actions from "./periodAdhesionFormActions"
import {InputDate} from "../../../framework/components/form/InputDate";
import {PeriodValidation, periodValidationToPeriod} from "../../../common/types/Period";
import {Action} from "fluxx";
import {FieldValidation} from "../../../framework/utils/Validation";
import PanelError from "../../../framework/components/form/PanelError";
import {PeriodAdhesionCreation} from "../adhesionModel";
import {createPeriodAdhesion} from "../adhesionApi";

export interface PeriodAdhesionFormProps {
}

interface PeriodConfiguration {
  startName: string
  endName: string
  startLabel: string
  endLabel: string
  startAction: Action<Date>
  endAction: Action<Date>
  period: FieldValidation<PeriodValidation>
}

export default class PeriodAdhesionForm extends StoreListenerComponent<PeriodAdhesionFormProps, PeriodAdhesionFormState> {
  constructor() {
    super(periodAdhesionFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    actions.initializeEmpty()
  }

  render(props: PeriodAdhesionFormProps, state: PeriodAdhesionFormState) {
    if (this.mounted) {
      return (
        <Page title="Créer une nouvelle période d'adhésion" role="admin">
          <Loading loading={state.step === "loading"} message={messages.user.adhesion.formPeriod.loading}>
            {
              state.step === "form"
                ? this.renderForm(props, state)
                : this.renderSuccess()
            }
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderForm(props: PeriodAdhesionFormProps, state: PeriodAdhesionFormState) {
    return (
      <Form
        submit="Créer la période"
        onSubmit={this.onSubmit}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        {this.renderPeriod(state)}
        {this.renderRegistrationPeriod(state)}
        {this.renderActivities(state)}
      </Form>
    )
  }

  renderPeriod(state: PeriodAdhesionFormState) {
    return this.renderPeriodLine({
      startName: "start",
      endName: "end",
      startLabel: "Début de la période",
      endLabel: "Fin de la période",
      startAction: actions.updateStart,
      endAction: actions.updateEnd,
      period: state.period
    })
  }

  renderRegistrationPeriod(state: PeriodAdhesionFormState) {
    return this.renderPeriodLine({
      startName: "registrationStart",
      endName: "registrationEnd",
      startLabel: "Début des inscription",
      endLabel: "Fin des inscriptions",
      startAction: actions.updateRegistrationStart,
      endAction: actions.updateRegistrationEnd,
      period: state.registrationPeriod
    })
  }

  renderPeriodLine(configuration: PeriodConfiguration) {
    return (
      <div>
        <PanelError fieldValidation={configuration.period}/>
        <div class="row">
          <div class="col-fill">
            <InputDate
              id={configuration.startName}
              label={configuration.startLabel}
              name={configuration.startName}
              required
              fieldValidation={configuration.period.value.start}
              onUpdate={configuration.startAction}
            />
          </div>
          <div class="col-fill">
            <InputDate
              id={configuration.endName}
              label={configuration.endLabel}
              name={configuration.endName}
              required
              fieldValidation={configuration.period.value.end}
              onUpdate={configuration.endAction}
            />
          </div>
        </div>
      </div>
    )
  }

  renderActivities(state: PeriodAdhesionFormState) {
    return (
      <div>
        <h2>Activités de la période</h2>

        <PanelError fieldValidation={state.activities}/>

        {state.activities.value.map(this.renderActivity)}

        <SecondaryButton message="Ajouter une activité" action={actions.addActivity}/>
      </div>
    )
  }

  renderActivity(activity: FieldValidation<string>, index: number) {
    return (
      <div class="row middle">
        <div class="col-fill">
          <Input
            id={`activity-${index}`}
            label="Activité"
            name={`activity-${index}`}
            type="text"
            placeholder="Nom de l'activité ou groupe (exemple: Ateliers théâtre)"
            required
            onUpdate={value => actions.updateActivity({index, value})}
            fieldValidation={activity}
          />
        </div>
        <div class="col">
          <IconDeleteButton message="x" action={() => actions.removeActivity(index)}/>
        </div>
      </div>
    )
  }

  renderSuccess() {
    return (
      <Panel type="success">
        <p>
          La période a bien été créée.
        </p>
        <p>
          <PrimaryButton message="Revenir à la page d'accueil" href="/"/>
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedPeriodAdhesion: PeriodAdhesionCreation = {
      period: periodValidationToPeriod(this.state.period.value),
      registrationPeriod: periodValidationToPeriod(this.state.registrationPeriod.value),
      activities: this.state.activities.value.map(_ => _.value)
    }
    createPeriodAdhesion(normalizedPeriodAdhesion)
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}