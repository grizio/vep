import preact from "preact"
import {LoginState, loginStore} from "./loginStore"
import {closeErrors, success, updateEmail, updateErrors, updatePassword} from "./loginActions"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import {login} from "../sessionApi";
import StoreListenerComponent from "../../../framework/utils/dom"
import * as sessionActions from "../../../framework/session/sessionActions"

export interface LoginProps {
  path: string
}

export default class Login extends StoreListenerComponent<LoginProps, LoginState> {
  constructor() {
    super(loginStore())
  }

  render(props: LoginProps, state: LoginState) {
    if (this.mounted) {
      return (
        <Page title="Connexion">
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

  renderForm(state: LoginState) {
    return (
      <Form
        submit="Se connecter"
        onSubmit={this.onSubmit}
        cancel="Revenir à l'accueil"
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
      </Form>
    )
  }

  renderSuccess() {
    return (
      <Panel type="success">
        <p>
          Vous êtes maintenant connecté.
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    login({
      email: this.state.email.value,
      password: this.state.password.value
    })
      .then(_ => sessionActions.login({
        email: this.state.email.value,
        date: _.date,
        token: _.token
      }))
      .then(() => success())
      .catch(errors => updateErrors(errors))
  }
}