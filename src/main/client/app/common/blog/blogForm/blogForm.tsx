import preact from "preact"
import Page from "../../../framework/components/Page"
import Form from "../../../framework/components/form/Form"
import Input from "../../../framework/components/form/Input"
import Panel from "../../../framework/components/Panel"
import StoreListenerComponent from "../../../framework/utils/dom"
import {PrimaryButton} from "../../../framework/components/buttons";
import Loading from "../../../framework/components/Loading";
import messages from "../../../framework/messages";
import {BlogFormState, blogFormStore} from "./blogFormStore";
import {createBlog, findBlog, updateBlog} from "../blogApi";
import * as actions from "./blogFormActions"
import {BlogCreation, BlogUpdate} from "../blogModel";
import RichInput from "../../../framework/components/form/RichInput";

export interface BlogFormProps {
  id?: string
}

export default class BlogForm extends StoreListenerComponent<BlogFormProps, BlogFormState> {
  constructor() {
    super(blogFormStore())
  }

  componentDidMount() {
    super.componentDidMount()
    if (this.props.id) {
      findBlog(this.props.id).then(actions.initialize)
    } else {
      actions.initializeEmpty()
    }
  }

  render(props: BlogFormProps, state: BlogFormState) {
    if (this.mounted) {
      return (
        <Page title={props.id ? "Modifier un article du blog" : "Créer un nouvel article du blog"} role="admin">
          <Loading loading={state.step === "loading"} message={messages.common.blog.form.loading}>
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

  renderForm(props: BlogFormProps, state: BlogFormState) {
    return (
      <Form
        submit={props.id ? "Modifier l'article" : "Créer l'article"}
        onSubmit={this.onSubmit}
        errors={state.errors}
        closeErrors={actions.closeErrors}
      >
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

  renderSuccess(props: BlogFormProps, state: BlogFormState) {
    return (
      <Panel type="success">
        <p>
          {props.id ? "L'article a bien été modifié." : "L'article a bien été créé."}
        </p>
        <p>
          <PrimaryButton message="Revenir à l'accueil" href="/" />
        </p>
      </Panel>
    )
  }

  onSubmit = () => {
    const normalizedBlog: BlogCreation | BlogUpdate = {
      id: this.props.id,
      title: this.state.title.value,
      content: this.state.content.value
    }
    const action = this.props.id
      ? updateBlog(normalizedBlog)
      : createBlog(normalizedBlog)
    action
      .then(() => actions.success())
      .catch(errors => actions.updateErrors(errors))
  }
}