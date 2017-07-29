import preact from "preact"
import {AsyncPage} from "../../../framework/components/Page"
import {RichContent} from "../../../framework/components/RichContent";
import {PageReadState, pageReadStore} from "./pageReadStore";
import {findPage} from "../pageApi";
import * as actions from "./pageReadActions"

export interface PageReadProps {
  path: string
  canonical?: string
}

export default class PageRead extends AsyncPage<PageReadProps, PageReadState> {
  constructor() {
    super(pageReadStore)
  }

  getTitle(props: PageReadProps, state: PageReadState): string {
    if (state.page) {
      return state.page.title
    } else {
      return ""
    }
  }

  initialize(props: PageReadProps) {
    return findPage(props.canonical)
      .then(page => actions.initialize(page))
  }

  renderPage(props: PageReadProps, state: PageReadState) {
    return (
      <RichContent content={state.page.content}/>
    )
  }
}