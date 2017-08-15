import preact from "preact"
import {FieldValidation} from "../../utils/Validation"
import {RichContent} from "../RichContent"
import {FlatSecondaryButton} from "../buttons";
import popinContent from "../popin/PopinContent";

interface RichInputProps {
  id: string
  label: string
  name: string
  placeholder?: string
  required?: boolean
  disabled?: boolean

  onUpdate(value: string): void

  fieldValidation: FieldValidation<string>
}

interface RichInputState {
  focused: boolean
}

export default class RichInput extends preact.Component<RichInputProps, RichInputState> {
  render(props: RichInputProps, state: RichInputState) {
    if (props.fieldValidation) {
      const label = props.required ? `${props.label} *` : props.label
      return (
        <div class="rich-input">
          <label for={props.id} class={this.getClass(props, state)}>{label}</label>
          <FlatSecondaryButton action={this.showHelp} message="ℹ️"/>
          <div class="row">
            <div class="col-1">
              {this.renderInput(props, state)}
            </div>
            <div class="col-1">
              <div class="output">
                <RichContent content={props.fieldValidation.value}/>
              </div>
            </div>
          </div>
          {this.renderError(props, state)}
        </div>
      )
    } else {
      return null
    }
  }

  renderInput = (props: RichInputProps, state: RichInputState) => {
    return (
      <div class="input">
        <div class="textarea-container">
          <textarea
            name={props.name}
            id={props.id}
            placeholder={props.placeholder || props.label}
            required={props.required}
            disabled={props.disabled}
            onInput={this.onInput}
            onFocus={this.onFocus}
            onBlur={this.onBlur}
            class={this.getClass(props, state)}
            value={props.fieldValidation ? props.fieldValidation.value || "" : ""}
          />
        </div>
        <pre class="rich-input-content">{this.format(props.fieldValidation.value)}</pre>
      </div>
    )
  }

  format = (content: string) => {
    return content
      .split("\n")
      .map(_ => _ + "\n")
      .map(this.formatLine)
  }

  formatLine = (line: string) => {
    if (line.startsWith("####")) {
      return <p class="h5">{line}</p>
    } else if (line.startsWith("###")) {
      return <p class="h4">{line}</p>
    } else if (line.startsWith("##")) {
      return <p class="h3">{line}</p>
    } else if (line.startsWith("#")) {
      return <p class="h2">{line}</p>
    } else if (line.startsWith("*")) {
      return <p class="ul">{line}</p>
    } else if (line.startsWith(">")) {
      return <p class="blockquote">{line}</p>
    } else if (line.startsWith("---")) {
      return <p class="hr">{line}</p>
    } else if (line.trim().length) {
      return <p class="p">{line}</p>
    } else {
      return <p class="empty">{line}</p>
    }
  }

  onInput = (e: Event) => {
    this.props.onUpdate((e.target as any).value)
  }

  onFocus = () => {
    this.setState({focused: true})
  }

  onBlur = () => {
    this.setState({focused: false})
  }

  showHelp = () => {
    popinContent("Aide sur les styles", HelpBox)
  }

  renderError = (props: RichInputProps, state: RichInputState) => {
    const fieldValidation = props.fieldValidation
    if (this.errorIsShown(fieldValidation)) {
      return fieldValidation.errors.map((error, i) =>
        <span class={this.getClass(props, state) + " error-message"} key={i.toString()}>{fieldValidation.errors}</span>
      )
    } else {
      // Force the component having an height by not being empty.
      return <span class={this.getClass(props, state) + " error-message none"}>&nbsp;</span>
    }
  }

  getClass = (props: RichInputProps, state: RichInputState) => {
    const classes = ["simple"]
    if (this.errorIsShown(props.fieldValidation)) classes.push("error")
    if (this.successIsShown(props.fieldValidation)) classes.push("success")
    if (state.focused) classes.push("focused")
    return classes.join(" ")
  }

  errorIsShown = (fieldValidation: FieldValidation<string>) => {
    return fieldValidation && fieldValidation.errors && fieldValidation.errors.length && fieldValidation.changed
  }

  successIsShown = (fieldValidation: FieldValidation<string>) => {
    return fieldValidation && (!fieldValidation.errors || !fieldValidation.errors.length) && fieldValidation.changed
  }
}

const HelpBox = (
  <div>
    <table>
      <thead>
      <tr>
        <th>Type</th>
        <th>Code</th>
        <th>Résultat</th>
      </tr>
      </thead>
      <tbody>

      <tr><th colSpan={3}>Blocs de texte</th></tr>

      <HelpLine type="Titre niveau 1" code="# Mon titre">
        <h2>Mon titre</h2>
      </HelpLine>
      <HelpLine type="Titre niveau 2" code="## Mon titre">
        <h3>Mon titre</h3>
      </HelpLine>
      <HelpLine type="Titre niveau 3" code="### Mon titre">
        <h4>Mon titre</h4>
      </HelpLine>
      <HelpLine type="Titre niveau 4" code="#### Mon titre">
        <h5>Mon titre</h5>
      </HelpLine>
      <HelpLine type="Liste à puce" code="* ma puce">
        <ul><li>ma puce</li></ul>
      </HelpLine>
      <HelpLine type="Citation" code="> Ma citation">
        <blockquote><p>Ma citation</p></blockquote>
      </HelpLine>
      <HelpLine type="Line horizontale" code="---">
        <hr />
      </HelpLine>
      <HelpLine type="Par défaut" code="Mon paragraphe">
        <p>Mon paragraphe</p>
      </HelpLine>

      <tr><th colSpan={3}>En ligne</th></tr>

      <HelpLine type="Gras" code="Mon **texte** en gras">
        Mon <strong>texte</strong> en gras
      </HelpLine>
      <HelpLine type="Italique" code="Mon //texte// en italique">
        Mon <em>texte</em> en italique
      </HelpLine>
      <HelpLine type="Lien" code="Un lien : http://voir-entendre-posso.fr">
        Un lien : <a href="Un lien : http://voir-entendre-posso.fr">http://voir-entendre-posso.fr</a>
      </HelpLine>
      <HelpLine type="Lien avec texte" code="Un lien : [Voir & Entendre](http://voir-entendre-posso.fr)">
        Un lien : <a href="Un lien : http://voir-entendre-posso.fr">Voir &amp; Entendre</a>
      </HelpLine>
      <HelpLine type="Une image" code="[[/assets/logo-acve.png]]">
        <img src="/assets/logo-acve.png" />
      </HelpLine>
      </tbody>
    </table>
  </div>
)

interface HelpLineProps {
  type: string
  code: string
  children?: Array<JSX.Element>
}

function HelpLine(props: HelpLineProps) {
  return (
    <tr>
      <td>{props.type}</td>
      <td><code>{props.code}</code></td>
      <td>{props.children}</td>
    </tr>
  )
}