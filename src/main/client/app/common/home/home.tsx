import preact from "preact"
import {AsyncPage} from "../../framework/components/Page"
import Panel, {PanelType} from "../../framework/components/Panel"
import messages from "../../framework/messages"
import {HomeState, homeStore} from "./homeStore";
import * as actions from "./homeActions"
import {longDateTimeFormat} from "../../framework/utils/dates";
import {RichContent} from "../../framework/components/RichContent";
import {OnGranted} from "../../framework/components/Security";
import {PrimaryButton, SecondaryButton} from "../../framework/components/buttons";
import {findLastBlog} from "../blog/blogApi";
import {Blog} from "../blog/blogModel";

interface HomeProps {
  path: string
  type?: string
  message?: string
}

export default class Home extends AsyncPage<HomeProps, HomeState> {
  constructor() {
    super(homeStore)
  }


  initialize(props: HomeProps): Promise<any> {
    return findLastBlog()
      .then(actions.initialize);
  }


  getTitle(props: HomeProps, state: HomeState): string {
    return "Voir & Entendre • La Possonnière";
  }


  renderPage(props: HomeProps, state: HomeState): preact.VNode {
    return (
      <div>
        {this.renderMessage(props)}
        {state.blogs.map((blog, index) => (
          <div>
            {this.renderBlog(blog)}
            {index < state.blogs.length - 1 && <hr/>}
          </div>
        ))}
        <CreateBlogButton/>
      </div>
    )
  }

  renderMessage(props: HomeProps) {
    if (props.type && props.message) {
      return (
        <Panel type={props.type as PanelType}>
          <p>
            {messages.find(props.message)}
          </p>
        </Panel>
      )
    } else {
      return null
    }
  }

  renderBlog = (blog: Blog) => {
    return (
      <section>
        <h2>{blog.title}</h2>
        <h5>
          <time dateTime={blog.date.toISOString()}>{longDateTimeFormat(blog.date)}</time>
        </h5>
        <RichContent content={blog.content}/>
        <UpdateBlogButton id={blog.id} />
      </section>
    )
  }
}

const CreateBlogButton = OnGranted(() => {
  return (
    <div>
      <hr/>
      <PrimaryButton message="Nouveau blog" href="/blog/create"/>
    </div>
  )
}, "admin")

interface UpdateBlogButtonProps {
  id: string
}

const UpdateBlogButton = OnGranted<UpdateBlogButtonProps>((props: UpdateBlogButtonProps) => {
  return (
    <SecondaryButton message="Modifier" href={`/blog/update/${props.id}`}/>
  )
}, "admin")