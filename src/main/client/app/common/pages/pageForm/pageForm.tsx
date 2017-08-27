import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input, {InputNumber} from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import StoreListenerComponent from "../../../framework/utils/dom"
import {PrimaryButton} from "../../../framework/components/buttons";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import {PageFormState, pageFormStore} from "./pageFormStore";
import * as actions from "./pageFormActions"
import {PageInformation} from "../pageModel";
import {createPage, findPage, updatePage} from "../pageApi";
import RichInput from "../../../framework/components/form/RichInput";

export interface PageFormProps {
  canonical?: string
}

export default class PageForm extends StoreListenerComponent<PageFormProps, PageFormState> {
  constructor() {
    super(pageFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    if (this.props.canonical) {
      findPage(this.props.canonical).then(actions.initialize)
    } else {
      actions.initializeEmpty()
    }
  }

  render(props: PageFormProps, state: PageFormState) {
    if (this.mounted) {
      return (
        <Page title={props.canonical ? "Modifier une page" : "Créer une nouvelle page"} role="admin">
          <Loading loading={state.step === "loading"} message={messages.production.company.form.loading}>
            {
              state.step === "form"
                ? this.renderForm(props, state)
                : this.renderSuccess(props, state)
            }
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderForm(props: PageFormProps, state: PageFormState) {
    return (
      <Form
        submit={props.canonical ? "Modifier la page" : "Créer la page"}
        onSubmit={this.onSubmit}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Input
          id="canonical"
          label="Url de la page"
          name="title"
          type="text"
          placeholder="Uniquement des chiffres, lettres et le symbol « - »"
          required
          disabled={!!props.canonical}
          onUpdate={actions.updateCanonical}
          fieldValidation={state.canonical}
        />

        <Input
          id="title"
          label="Title"
          name="title"
          type="text"
          placeholder="Titre de la page"
          required
          onUpdate={actions.updateTitle}
          fieldValidation={state.title}
        />

        <InputNumber
          id="order"
          label="Ordre dans le menu"
          name="order"
          placeholder="0 pour l'exclure"
          onUpdate={actions.updateOrder}
          fieldValidation={state.order}
        />

        <RichInput
          id="content"
          label="Contenu de la page"
          name="content"
          placeholder="Contenu de la page, tel que vous souhaitez l'afficher"
          required
          onUpdate={actions.updateContent}
          fieldValidation={state.content}
        />
      </Form>
    )
  }

  renderSuccess(props: PageFormProps, state: PageFormState) {
    return (
      <Panel type="success">
        <p>
          {props.canonical ? "La page a bien été modifiée." : "La page a bien été créée."}
        </p>
        <p>
          <PrimaryButton message="Revenir à la page" href={`/page/${state.canonical.value}`} />
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedPage: PageInformation = {
      canonical: this.state.canonical.value,
      title: this.state.title.value,
      order: this.state.order.value || 0,
      content: this.state.content.value
    }
    const action = this.props.canonical
      ? updatePage(normalizedPage)
      : createPage(normalizedPage)
    action
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}