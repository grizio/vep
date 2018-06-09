import preact from 'preact'
import { Link, route } from 'preact-router/src'
import Form from '../../../framework/components/form/Form'
import Input from '../../../framework/components/form/Input'
import { AsyncPage } from '../../../framework/components/Page'
import Panel from '../../../framework/components/Panel'
import * as sessionActions from '../../../framework/session/sessionActions'
import { deleteAccount, getCurrentProfile } from '../profileApi'
import * as actions from './deleteAccountActions'
import { DeleteAccountState, deleteAccountStore } from './deleteAccountStore'

export interface DeleteAccountProps {
  path: string
}

export default class DeleteAccount extends AsyncPage<DeleteAccountProps, DeleteAccountState> {
  constructor() {
    super(deleteAccountStore)
  }

  initialize(props: DeleteAccountProps) {
    return getCurrentProfile()
      .then((profile) => actions.initialize(profile))
  }

  getTitle(props: DeleteAccountProps, state: DeleteAccountState): string {
    return 'Supprimer votre compte'
  }

  renderPage(props: DeleteAccountProps, state: DeleteAccountState): preact.VNode {
    return <div>
      {this.renderWarning()}
      {this.renderForm(state)}
    </div>
  }

  renderWarning() {
    return (
      <Panel type="error">
        <p>
          Cette page vous permet de supprimer votre compte.
          Il ne sera pas possible de revenir en arrière.
        </p>
        <p>
          Si vous souhaitez réellement supprimer votre compte,
          veuillez compléter le formulaire ci-dessous.
        </p>
        <p>
          Si vous souhaitez plus avoir d'information,
          vous pouvez <Link href="/contact">nous contacter</Link>.
        </p>
      </Panel>
    )
  }

  renderForm(state: DeleteAccountState) {
    return (
      <Form
        submit={'Supprimer définitivement mon compte'}
        onSubmit={this.onSubmit}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Input
          id="password"
          label="Votre mot de passe"
          name="password"
          type="password"
          placeholder="Le mot de passe utilisé pour vous connecter"
          required
          onUpdate={actions.updatePassword}
          fieldValidation={state.password}
        />
      </Form>
    )
  }

  onSubmit = () => {
    deleteAccount(this.state.profile.email, this.state.password.value)
      .then(_ => {
        sessionActions.logout()
        route('/')
      })
      .catch(actions.updateErrors)
  }
}