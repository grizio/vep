import preact from "preact"
import {AsyncPage} from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import {IconDeleteButton, PrimaryButton, SecondaryButton} from "../../../framework/components/buttons";
import {PhoneValidation, ProfileFormState, profileFormStore} from "./profileFormStore";
import * as actions from "./profileFormActions"
import {getCurrentProfile, updateProfile} from "../profileApi";
import {FieldValidation} from "../../../framework/utils/Validation";
import {UserRole} from "../../../framework/session/sessionStore";
import {Profile} from "../profileModel";

export interface ProfileFormProps {
  path: string
}

export default class ProfileForm extends AsyncPage<ProfileFormProps, ProfileFormState> {
  constructor() {
    super(profileFormStore)
  }

  initialize() {
    return getCurrentProfile().then(actions.initialize)
  }

  getTitle(props: ProfileFormProps, state: ProfileFormState) {
    if (state.email) {
      return `Modifier votre profil (${state.email})`
    } else {
      return "Modifier votre profil"
    }
  }

  getRole(): UserRole {
    return "user"
  }

  renderPage(props: ProfileFormProps, state: ProfileFormState) {
    if (state.step === "form") {
      return this.renderForm(props, state)
    } else {
      return this.renderSuccess()
    }
  }

  renderForm(props: ProfileFormProps, state: ProfileFormState) {
    return (
      <Form
        submit="Modifier mon profil"
        onSubmit={this.onSubmit}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Input
          id="firstName"
          label="Prénom"
          name="fisrtName"
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
          placeholder="Votre nom de famille"
          required
          onUpdate={actions.updateLastName}
          fieldValidation={state.lastName}
        />

        <Input
          id="address"
          label="Adresse"
          name="address"
          type="text"
          placeholder="Votre adresse postale"
          required
          onUpdate={actions.updateAddress}
          fieldValidation={state.address}
        />

        <Input
          id="zipCode"
          label="Code postal"
          name="zipCode"
          type="text"
          placeholder="Code postal sur cinq chiffres"
          pattern="^[0-9]{5}$"
          required
          onUpdate={actions.updateZipCode}
          fieldValidation={state.zipCode}
        />

        <Input
          id="city"
          label="Ville"
          name="city"
          type="text"
          placeholder="Ville"
          required
          onUpdate={actions.updateCity}
          fieldValidation={state.city}
        />

        {this.renderPhones(state.phones)}
      </Form>
    )
  }

  renderPhones(phones: FieldValidation<Array<PhoneValidation>>) {
    return (
      <div>
        <h2>Numéros de téléphones</h2>

        {
          (phones.changed && phones.errors && phones.errors.length)
            ? (
              <Panel type="error">
                {phones.errors.map(error => <p>{error}</p>)}
              </Panel>
            ) : null
        }

        {phones.value.map(this.renderPhone)}

        <SecondaryButton message="Ajouter un téléphone" action={() => actions.addPhone()}/>
      </div>
    )
  }

  renderPhone(phone: PhoneValidation, index: number) {
    return (
      <div class="row middle">
        <div class="col-fill">
          <Input
            id={`phone-${index}-name`}
            label="Nom"
            name={`phone-${index}-name`}
            type="text"
            placeholder="ex: domicile, portable, principal…"
            required
            onUpdate={value => actions.updatePhoneName({index, value})}
            fieldValidation={phone.name}
          />
        </div>
        <div class="col-fill">
          <Input
            id={`phone-${index}-number`}
            label="Numéro"
            name={`phone-${index}-number`}
            type="text"
            placeholder="Numéro de téléphone, commaençant par 0X ou +33X"
            required
            onUpdate={value => actions.updatePhoneNumber({index, value})}
            fieldValidation={phone.number}
          />
        </div>
        <div class="col">
          <IconDeleteButton message="x" action={() => actions.removePhone(index)}/>
        </div>
      </div>
    )
  }

  renderSuccess() {
    return (
      <Panel type="success">
        <p>
          Votre profil a bien été mis à jour.
        </p>
        <p>
          <PrimaryButton message="Revenir à l'accueil" href="/"/>
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedProfile: Profile = {
      email: this.state.email,
      firstName: this.state.firstName.value,
      lastName: this.state.lastName.value,
      address: this.state.address.value,
      zipCode: this.state.zipCode.value,
      city: this.state.city.value,
      phones: this.state.phones.value.map(phone => ({
        name: phone.name.value,
        number: phone.number.value
      }))
    }
    updateProfile(normalizedProfile)
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}