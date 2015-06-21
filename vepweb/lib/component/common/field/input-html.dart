part of vep.component.common.field;

@Component(
    selector: 'input-html',
    templateUrl: '/packages/vepweb/component/common/field/input-html.html',
    useShadowDom: false)
class InputHtmlComponent extends FieldComponent<String> {
  @NgOneWay('maxlength')
  int maxLength;

  @NgAttr('placeholder')
  String placeholder;
}

@Decorator(selector: 'textarea.input-html')
class InputHtmlDecorator extends FieldDecorator {
  InputHtmlDecorator(Element element) : super(element);

  @override
  set scope(Scope scope) {
    super.scope = scope;
    if (scope.context is InputHtmlComponent) {
      var ctx = scope.context as InputHtmlComponent;
      var obj = context['UIkit'];
      obj.callMethod('htmleditor', [element]);
      var htmlEditorInner = context['\$'].apply([element]).callMethod('data', ['htmleditor'])['editor'];
      htmlEditorInner.callMethod('on', ['change', (a, b) {
        ctx.value = (element as TextAreaElement).value;
      }]);
    }
  }

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    if (scope.context is InputHtmlComponent) {
      var ctx = scope.context as InputHtmlComponent;
      addAttribute('maxlength', ctx.maxLength);
      addAttribute('placeholder', ctx.placeholder);
    }
  }
}