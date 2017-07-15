import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import {sendContactMessage} from "../contactApi";
import StoreListenerComponent from "../../../framework/utils/dom"
import {ContactState, contactStore} from "./contactStore";
import * as actions from "./contactActions";

export interface ContactProps {
  path: string
}

export default class Contact extends StoreListenerComponent<ContactProps, ContactState> {
  constructor() {
    super(contactStore())
  }

  render(props: ContactProps, state: ContactState) {
    if (this.mounted) {
      return (
        <Page title="Nous contacter">
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

  renderForm(state: ContactState) {
    return (
      <Form
        submit="Envoyer"
        onSubmit={this.onSubmit}
        cancel="Revenir à l'accueil"
        onCancel="/"
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Input
          id="name"
          label="Votre nom"
          name="name"
          type="text"
          placeholder="Prénom Nom"
          required
          onUpdate={actions.updateName}
          fieldValidation={state.name}
        />

        <Input
          id="email"
          label="Adresse e-mail"
          name="email"
          type="email"
          placeholder="Votre adresse e-mail"
          required
          onUpdate={actions.updateEmail}
          fieldValidation={state.email}
        />

        <Input
          id="email2"
          label="Confirmation"
          name="email2"
          type="email"
          placeholder="Veuillez confirmer votre adresse e-mail"
          required
          onUpdate={actions.updateEmail2}
          fieldValidation={state.email2}
        />

        <Input
          id="title"
          label="Titre"
          name="title"
          type="text"
          placeholder="Titre de votre message"
          required
          onUpdate={actions.updateTitle}
          fieldValidation={state.title}
        />

        <Input
          id="content"
          label="Contenu"
          name="content"
          type="textarea"
          placeholder="Contenu de votre message. Merci de préciser le contexte si possible."
          required
          onUpdate={actions.updateContent}
          fieldValidation={state.content}
        />
      </Form>
    )
  }

  renderSuccess() {
    return (
      <Panel type="success">
        <p>
          Votre message a été envoyé.
          Nous y répondrons dès que possible à l'adresse e-mail que vous avez indiquée.
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    sendContactMessage({
      name: this.state.name.value,
      email: this.state.email.value,
      title: this.state.title.value,
      content: this.state.content.value
    })
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}