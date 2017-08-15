import preact from "preact"
import Page from "../../../framework/components/Page"
import {RegisterState, registerStore} from "./registerStore"
import Input from "../../../framework/components/form/Input"
import StoreListenerComponent from "../../../framework/utils/dom"
import Form from "../../../framework/components/form/Form"
import {closeErrors, success, updateEmail, updateErrors, updatePassword, updatePassword2} from "./registerActions"
import {Link} from "preact-router/src"
import {register} from "../registerApi";
import Panel from "../../../framework/components/Panel"

export interface RegisterProps {
  path: string
}

export default class Register extends StoreListenerComponent<RegisterProps, RegisterState> {
  constructor() {
    super(registerStore())
  }

  render(props: RegisterProps, state: RegisterState) {
    if (this.mounted) {
      return (
        <Page title="Inscription">
          {
            state.step === "form"
              ? this.renderForm(state)
              : this.renderSuccess(state)
          }
        </Page>
      )
    } else {
      return null
    }
  }

  renderForm(state: RegisterState) {
    return (
      <Form
        submit="Valider l'inscription"
        onSubmit={this.onSubmit}
        cancel="Annuler et revenir à l'accueil"
        onCancel="/"
        errors={state.errors}
        closeErrors={closeErrors}
      >
        <Input
          id="email"
          label="Adresse e-mail"
          name="email"
          type="email"
          placeholder="Votre adresse e-mail"
          required
          onUpdate={updateEmail}
          fieldValidation={state.email}
        />

        <Input
          id="password"
          label="Mot de passe"
          name="password"
          type="password"
          placeholder="Votre mot de passe"
          required
          onUpdate={updatePassword}
          fieldValidation={state.password}
        />

        <Input
          id="password2"
          label="Confirmation"
          name="password2"
          type="password"
          placeholder="Confirmer votre mot de passe"
          required
          onUpdate={updatePassword2}
          fieldValidation={state.password2}
        />
      </Form>
    )
  }

  renderSuccess(state: RegisterState) {
    return (
      <Panel type="success">
        <p>
          Votre inscription a été prise en compte.
        </p>
        <p>
          Vous devriez recevoir un mail à l'adresse que vous avez indiquée ({state.email.value}) d'ici quelques minutes.
          Ce mail contient un lien permettant de valider l'inscription.
          Veuillez accéder à ce lien avant de vous connecter.
          Une fois l'inscription validée, vous pourrez vous connecter au site avec votre mot de passe.
        </p>
        <p>
          Si vous ne recevez pas le mail d'ici quelques minutes, veuillez <Link href="/contact">nous contacter</Link>.
        </p>
        <p>
          Voir &amp; Entendre ne vous demandera jamais vos identifiants.
          Si vous recevez un mail en notre nom vous demandant vos identifiants,
          veuillez ignorer le mail et <Link href="/contact">nous contacter</Link>,
          il s'agit surement d'une tentative de phishing.
          De même, Voir &amp; Entendre ne vous demandera jamais vos informations bancaires.
          Si vous avez le moindre doute, n'hésitez pas à <Link href="/contact">nous contacter</Link>.
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    register({
      email: this.state.email.value,
      password: this.state.password.value
    })
      .then(success)
      .catch(errors => updateErrors(errors))
  }
}