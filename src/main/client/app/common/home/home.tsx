import preact from "preact"
import {AsyncPage} from "../../framework/components/Page"
import Panel, {PanelType} from "../../framework/components/Panel"
import messages from "../../framework/messages"
import {HomeState, homeStore} from "./homeStore";
import * as actions from "./homeActions"
import {RichContent} from "../../framework/components/RichContent";
import {OnGranted} from "../../framework/components/Security";
import {PrimaryButton} from "../../framework/components/buttons";
import {findPage} from "../pages/pageApi";
import {Function1} from "../../framework/lib";
import {PageInformation} from "../pages/pageModel";

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
    return findPage("home")
      .then(actions.initialize);
  }

  getTitle(props: HomeProps, state: HomeState): string {
    return state.page ? state.page.title : "Voir & Entendre • La Possonnière";
  }

  renderPage(props: HomeProps, state: HomeState): preact.VNode {
    return (
      <div>
        {this.renderMessage(props)}
        {
          !!state.page && this.renderContent(state.page)
        }
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

  renderContent = (page: PageInformation) => {
    return (
      <div>
        <RichContent content={page.content}/>
        <UpdatePageButton canonical={page.canonical}/>
      </div>
    )
  }
}

interface UpdatePageButtonProps {
  canonical: string
}

const UpdatePageButton: Function1<UpdatePageButtonProps, JSX.Element> = OnGranted((props: UpdatePageButtonProps) => {
  return (
    <PrimaryButton
      href={`/cms/page/update/${props.canonical}`}
      message="Modifier"
    />
  )
}, "admin")