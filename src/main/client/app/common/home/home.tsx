import preact from "preact"
import Page from "../../framework/components/Page"
import Panel, {PanelType} from "../../framework/components/Panel"
import messages from "../../framework/messages"

interface HomeProps {
  path: string
  type?: string
  message?: string
}

export default function Home(props: HomeProps) {
  return (
    <Page title="Voir & Entendre">
      {renderMessage(props)}
      <h1>Voir &amp Entendre</h1>
      <p>To be completed</p>
    </Page>
  )
}


function renderMessage(props: HomeProps) {
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