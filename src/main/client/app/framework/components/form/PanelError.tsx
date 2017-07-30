import preact from "preact"
import Panel from "../Panel"
import {FieldValidation} from "../../utils/Validation";

interface PanelErrorProps {
  fieldValidation: FieldValidation<any>
}

export default function PanelError(props: PanelErrorProps) {
  if (props.fieldValidation.errors && props.fieldValidation.errors.length) {
    return (
      <Panel type="error">
        {props.fieldValidation.errors.map(error => <p>{error}</p>)}
      </Panel>
    )
  } else {
    return null
  }
}