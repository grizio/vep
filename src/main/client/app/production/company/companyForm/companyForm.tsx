import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import StoreListenerComponent from "../../../framework/utils/dom"
import {CompanyFormState, companyFormStore} from "./companyFormStore";
import * as actions from "./companyFormActions";
import {createCompany, findCompany, updateCompany} from "../companyApi";
import {PrimaryButton} from "../../../framework/components/buttons";
import {Company, CompanyCreation} from "../companyModel";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import Switch from "../../../framework/components/form/Switch";
import RichInput from "../../../framework/components/form/RichInput";

export interface CompanyFormProps {
  id?: string
}

export default class CompanyForm extends StoreListenerComponent<CompanyFormProps, CompanyFormState> {
  constructor() {
    super(companyFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    if (this.props.id) {
      findCompany(this.props.id).then(actions.initialize)
    } else {
      actions.initializeEmpty()
    }
  }

  render(props: CompanyFormProps, state: CompanyFormState) {
    if (this.mounted) {
      return (
        <Page title={props.id ? "Modifier une troupe" : "Créer une nouvelle troupe"} role="admin">
          <Loading loading={state.step === "loading"} message={messages.production.company.form.loading}>
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

  renderForm(props: CompanyFormProps, state: CompanyFormState) {
    return (
      <Form
        submit={props.id ? "Modifier la troupe" : "Créer la troupe"}
        onSubmit={this.onSubmit}
        cancel="Revenir à la liste"
        onCancel="/production/companies"
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Input
          id="name"
          label="Nom de la troupe"
          name="name"
          type="text"
          placeholder="Nom de la troupe"
          required
          onUpdate={actions.updateName}
          fieldValidation={state.name}
        />

        <Input
          id="address"
          label="Adresse"
          name="address"
          type="text"
          placeholder="Adresse de la troupe (ex: théâtre où elle se produit habituellement ou ville d'origine)"
          required
          onUpdate={actions.updateAddress}
          fieldValidation={state.address}
        />

        <Switch
          id="isVep"
          label="Voir & Entendre"
          name="isVep"
          placeholderOff="Troupe extérieure"
          placeholderOn="Troupe de Voir & Entendre"
          onUpdate={actions.updateIsVep}
          fieldValidation={state.isVep}
        />

        <RichInput
          id="content"
          label="Description"
          name="content"
          placeholder="Description de la troupe, informations complémentaires"
          required
          onUpdate={actions.updateContent}
          fieldValidation={state.content}
        />
      </Form>
    )
  }

  renderSuccess(props: CompanyFormProps) {
    return (
      <Panel type="success">
        <p>
          {props.id ? "La troupe a bien été modifiée." : "La troupe a bien été créée."}
        </p>
        <p>
          <PrimaryButton message="Revenir à la liste" href="/production/companies" />
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedCompany: Company | CompanyCreation = {
      id: this.state.id,
      name: this.state.name.value,
      address: this.state.address.value,
      content: this.state.content.value,
      isVep: this.state.isVep.value
    }
    const action = normalizedCompany.id
      ? updateCompany(normalizedCompany)
      : createCompany(normalizedCompany)
    action
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}