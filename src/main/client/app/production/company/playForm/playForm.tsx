import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Panel from "../../../framework/components/Panel"
import Loading from "../../../framework/components/Loading";
import {Card, CardContent} from "../../../framework/components/card/Card";
import {RichContent} from "../../../framework/components/RichContent";
import {IconDeleteButton, PrimaryButton, SecondaryButton} from "../../../framework/components/buttons";
import messages from "../../../framework/messages";
import StoreListenerComponent from "../../../framework/utils/dom"
import {DateValidation, PlayFormState, playFormStore, PriceValidation} from "./playFormStore";
import * as actions from "./playFormActions";
import {createPlay, findCompany, findPlay, findShow, updatePlay} from "../companyApi";
import {Company, PlayCreation, PlayUpdate, Show} from "../companyModel";
import {findAllTheaters} from "../../theater/theaterApi";
import Select from "../../../framework/components/form/Select";
import {InputDateTime} from "../../../framework/components/form/InputDate";
import {FieldValidation} from "../../../framework/utils/Validation";
import Input, {InputNumber} from "../../../framework/components/form/Input";

export interface PlayFormProps {
  company: string
  show: string
  id?: string
}

export default class PlayForm extends StoreListenerComponent<PlayFormProps, PlayFormState> {
  constructor() {
    super(playFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    if (this.props.id) {
      Promise.all([
        findCompany(this.props.company),
        findShow(this.props.company, this.props.show),
        findPlay(this.props.company, this.props.show, this.props.id),
        findAllTheaters()
      ])
        .then(([company, show, play, theaters]) => actions.initialize({company, show, play, theaters}))
    } else {
      Promise.all([
        findCompany(this.props.company),
        findShow(this.props.company, this.props.show),
        findAllTheaters()
      ])
        .then(([company, show, theaters]) => actions.initialize({company, show, theaters}))
    }
  }

  render(props: PlayFormProps, state: PlayFormState) {
    if (this.mounted) {
      return (
        <Page title={props.id ? "Modifier une séance" : "Créer de nouvelles séances"} role="admin">
          <Loading loading={state.step === "loading"} message={messages.production.company.show.form.loading}>
            {state.step === "form" && this.renderEdition(props, state)}
            {state.step === "success" && this.renderSuccess(props, state)}
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderEdition(props: PlayFormProps, state: PlayFormState) {
    return (
      <div class="row">
        <div class="col-3">
          {this.renderForm(props, state)}
        </div>
        <div class="col-1">
          {this.renderCompanyCard(state.company)}
          {this.renderShowCard(state.show)}
        </div>
      </div>
    )
  }

  renderForm(props: PlayFormProps, state: PlayFormState) {
    return (
      <Form
        submit={props.id ? "Modifier la séance" : "Créer les séances"}
        onSubmit={this.onSubmit}
        cancel={`Revenir sur la fiche de ${state.company.name}`}
        onCancel={`/production/companies/page/${state.company.id}`}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Select
          id="theater"
          label="Théâtre"
          name="theater"
          placeholder="Veuillez choisir dans la liste"
          required
          options={state.availableTheaters.map(theater => ({label: theater.name, value: theater.id}))}
          fieldValidation={state.theater}
          onUpdate={actions.updateTheater}
        />

        {this.renderFormDates(state, state.dates)}
        {this.renderFormPrices(state.prices)}
      </Form>
    )
  }

  renderFormDates(state: PlayFormState, dates: FieldValidation<Array<DateValidation>>) {
    return (
      <div>
        <h2>Dates</h2>

        {
          (dates.changed && dates.errors && dates.errors.length)
            ? (
              <Panel type="error">
                {dates.errors.map(error => <p>{error}</p>)}
              </Panel>
            ) : null
        }

        {dates.value.map((date, index) => this.renderFormDate(state, date, index))}

        {!state.id && <SecondaryButton message="Ajouter une Date" action={() => actions.addDate()}/> }
      </div>
    )
  }

  renderFormDate(state: PlayFormState, date: DateValidation, index: number) {
    return (
      <div class="row middle">
        <div class="col-fill">
          <InputDateTime
            id="date"
            label="Date de représentation"
            name="date"
            required
            fieldValidation={date.date}
            onUpdate={value => actions.updateDate({index, value})}
          />
        </div>
        <div class="col-fill">
          <InputDateTime
            id="reservationEndDate"
            label="Date de fin des réservations"
            name="reservationEndDate"
            required
            fieldValidation={date.reservationEndDate}
            onUpdate={value => actions.updateReservationEndDate({index, value})}
          />
        </div>
        {
          !state.id &&
          <div class="col-shrink">
            <IconDeleteButton message="x" action={() => actions.removeDate(index)}/>
          </div>
        }
      </div>
    )
  }

  renderFormPrices(prices: FieldValidation<Array<PriceValidation>>) {
    return (
      <div>
        <h2>Tarifs</h2>

        {
          (prices.changed && prices.errors && prices.errors.length)
            ? (
              <Panel type="error">
                {prices.errors.map(error => <p>{error}</p>)}
              </Panel>
            ) : null
        }

        {prices.value.map(this.renderFormPrice)}

        <SecondaryButton message="Ajouter un tarif" action={() => actions.addPrice()}/>
      </div>
    )
  }

  renderFormPrice(price: PriceValidation, index: number) {
    return (
      <div class="row middle">
        <div class="col">
          <Input
            id={`price-${index}-name`}
            label="Nom du tarif"
            name={`price-${index}-name`}
            type="text"
            placeholder="Nom"
            required
            onUpdate={value => actions.updatePriceName({index, value})}
            fieldValidation={price.name}
          />
        </div>
        <div class="col">
          <InputNumber
            id={`price-${index}-value`}
            label="Prix (€)"
            name={`price-${index}-value`}
            placeholder="Prix"
            required
            onUpdate={value => actions.updatePriceValue({index, value})}
            fieldValidation={price.value}
          />
        </div>
        <div class="col-fill">
          <Input
            id={`price-${index}-condition`}
            label="Conditions"
            name={`price-${index}-condition`}
            type="textarea"
            placeholder="Ex: age, adhérents, etc."
            onUpdate={value => actions.updatePriceCondition({index, value})}
            fieldValidation={price.condition}
          />
        </div>
        <div class="col">
          <IconDeleteButton message="x" action={() => actions.removePrice(index)}/>
        </div>
      </div>
    )
  }

  renderCompanyCard(company: Company) {
    return (
      <Card title="Troupe">
        <CardContent>
          <p>{company.name}</p>
          <p>🏠 {company.isVep ? "Voir & Entendre" : "Troupe extérieure"}</p>
          <p>📍 {company.address}</p>
          <RichContent content={company.content} limit={100}/>
        </CardContent>
      </Card>
    )
  }

  renderShowCard(show: Show) {
    return (
      <Card title="Pièce">
        <CardContent>
          <p>{show.title}</p>
          <p>✍️ {show.author}</p>
          <p>🎭 {show.director}</p>
          <RichContent content={show.content} limit={100}/>
        </CardContent>
      </Card>
    )
  }

  renderSuccess(props: PlayFormProps, state: PlayFormState) {
    return (
      <Panel type="success">
        <p>
          {props.id ? "La séance a bien été modifiée." : "Les séances ont bien été créées."}
        </p>
        <p>
          <PrimaryButton message={`Revenir sur la fiche de ${state.show.title}`}
                         href={`/production/companies/${state.company.id}/shows/page/${state.show.id}`}/>
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedPlays: Array<PlayCreation | PlayUpdate> = this.getNormalizedPlays()
    const action = Promise.all(
      normalizedPlays.map(normalizedPlay => {
        (normalizedPlay as PlayUpdate).id
        ? updatePlay(this.state.company.id, this.state.show.id, normalizedPlay as PlayUpdate)
        : createPlay(this.state.company.id, this.state.show.id, normalizedPlay)
      })
    )
    action
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }

  getNormalizedPlays(): Array<PlayCreation> {
    return this.state.dates.value.map(date => ({
      id: this.state.id,
      theater: this.state.theater.value,
      date: date.date.value,
      reservationEndDate: date.reservationEndDate.value,
      prices: this.state.prices.value.map(price => ({
        name: price.name.value,
        value: price.value.value,
        condition: price.condition.value
      }))
    }))
  }
}