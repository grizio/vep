import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import Loading from "../../../framework/components/Loading";
import {Card, CardContent} from "../../../framework/components/card/Card";
import {RichContent} from "../../../framework/components/RichContent";
import {PrimaryButton} from "../../../framework/components/buttons";
import messages from "../../../framework/messages";
import StoreListenerComponent from "../../../framework/utils/dom"
import {ShowFormState, showFormStore} from "./showFormStore";
import * as actions from "./showFormActions";
import {createShow, findCompany, updateShow} from "../companyApi";
import {Company, Show, ShowCreation} from "../companyModel";

export interface ShowFormProps {
  company: string
  id?: string
}

export default class ShowForm extends StoreListenerComponent<ShowFormProps, ShowFormState> {
  constructor() {
    super(showFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    findCompany(this.props.company)
      .then(company => actions.initialize({company}))
  }

  render(props: ShowFormProps, state: ShowFormState) {
    if (this.mounted) {
      return (
        <Page title={props.id ? "Modifier une pièce" : "Créer une nouvelle pièce"} role="admin">
          <Loading loading={state.step === "loading"} message={messages.production.company.show.form.loading}>
            { state.step === "form" &&  this.renderEdition(props, state) }
            { state.step === "success" && this.renderSuccess(props, state) }
          </Loading>
        </Page>
      )
    } else {
      return null
    }
  }

  renderEdition(props: ShowFormProps, state: ShowFormState) {
    return (
      <div class="row">
        <div class="col-3">
          {this.renderForm(props, state)}
        </div>
        <div class="col-1">
          {this.renderCompanyCard(state.company)}
        </div>
      </div>
    )
  }

  renderCompanyCard(company: Company) {
    return (
      <Card title={company.name}>
        <CardContent>
          <p>📍 {company.address}</p>
          <RichContent content={company.content}/>
        </CardContent>
      </Card>
    )
  }

  renderForm(props: ShowFormProps, state: ShowFormState) {
    return (
      <Form
        submit={props.id ? "Modifier la pièce" : "Créer la pièce"}
        onSubmit={this.onSubmit}
        cancel={`Revenir sur la fiche de ${state.company.name}`}
        onCancel={`/production/companies/${state.company.id}`}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
        <Input
          id="title"
          label="Titre"
          name="title"
          type="text"
          placeholder="Titre de la pièce"
          required
          onUpdate={actions.updateTitle}
          fieldValidation={state.title}
        />

        <Input
          id="author"
          label="Auteur"
          name="author"
          type="text"
          placeholder="Auteur de la pièce"
          required
          onUpdate={actions.updateAuthor}
          fieldValidation={state.author}
        />

        <Input
          id="director"
          label="Metteur en scène"
          name="director"
          type="text"
          placeholder="Metteur en scène de la pièce"
          required
          onUpdate={actions.updateDirector}
          fieldValidation={state.director}
        />

        <Input
          id="content"
          label="Description"
          name="content"
          type="textarea"
          placeholder="Description de la pièce, informations complémentaires"
          required
          onUpdate={actions.updateContent}
          fieldValidation={state.content}
        />
      </Form>
    )
  }

  renderSuccess(props: ShowFormProps, state: ShowFormState) {
    return (
      <Panel type="success">
        <p>
          {props.id ? "La pièce a bien été modifiée." : "La pièce a bien été créée."}
        </p>
        <p>
          <PrimaryButton message={`Revenir sur la fiche de ${state.company.name}`}
                         href={`/production/companies/${state.company.id}`}/>
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedShow: Show | ShowCreation = {
      id: this.state.id,
      title: this.state.title.value,
      author: this.state.author.value,
      director: this.state.director.value,
      content: this.state.content.value
    }
    const action = normalizedShow.id
      ? updateShow(this.state.company.id, normalizedShow)
      : createShow(this.state.company.id, normalizedShow)
    action
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}