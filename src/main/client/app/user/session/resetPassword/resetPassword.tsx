import preact from "preact"
import {closeErrors, success, updateErrors, updatePassword, updatePassword2} from "./resetPasswordActions"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import {resetPassword} from "../sessionApi";
import StoreListenerComponent from "../../../framework/utils/dom"
import {PrimaryButton} from "../../../framework/components/buttons";
import {ResetPasswordState, resetPasswordStore} from "./resetPasswordStore";

export interface ResetPasswordProps {
  path: string
  email?: string
  token?: string
}

export default class ResetPassword extends StoreListenerComponent<ResetPasswordProps, ResetPasswordState> {
  constructor() {
    super(resetPasswordStore())
  }

  render(props: ResetPasswordProps, state: ResetPasswordState) {
    if (this.mounted) {
      return (
        <Page title="Réinitialiser mon mot de passe">
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

  renderForm(state: ResetPasswordState) {
    return (
      <Form
        submit="Réinitialiser mon mot de passe"
        onSubmit={this.onSubmit}
        cancel="Revenir à l'accueil"
        onCancel="/"
        errors={state.errors}
        closeErrors={closeErrors}
      >
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

  renderSuccess() {
    return (
      <Panel type="success">
        <p>
          Votre mot de passe a été mis à jour, vous pouvez l'utiliser pour vous connecter.
        </p>
        <p>
          <PrimaryButton message="Accéder à la page de connexion" href="/personal/login"/>
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    resetPassword({
      email: this.props.email,
      token: this.props.token,
      password: this.state.password.value
    })
      .then(() => success())
      .catch(errors => updateErrors(errors))
  }
}