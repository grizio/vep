part of vep.component.common.field;

/// This component provides a field for the user to enter HTML content.
@Component(
    selector: 'input-html',
    templateUrl: '/packages/vepweb/component/common/field/input-html.html',
    useShadowDom: false)
class InputHtmlComponent extends FieldComponent<String> {
  @NgOneWay('maxlength')
  int maxLength;

  @NgAttr('placeholder')
  String placeholder;

  InputHtmlDecorator inputHtmlDecorator;

  @override
  set value(String newValue) {
    if (_value != newValue) {
      super.value = newValue;
      if (inputHtmlDecorator != null) {
        inputHtmlDecorator.refreshEditor();
      }
    }
  }

  void _setValueInner(String newValue) {
    super.value = newValue;
  }
}

/// Decorates the input to add attributes in terms of developer configuration.
@Decorator(selector: 'textarea.input-html')
class InputHtmlDecorator extends FieldDecorator {
  InputHtmlDecorator(Element element) : super(element);

  @override
  set scope(Scope scope) {
    super.scope = scope;
    if (scope.context is InputHtmlComponent) {
      var ctx = scope.context as InputHtmlComponent;
      ctx.inputHtmlDecorator = this;
      context['UIkit'].callMethod('htmleditor', [element]);
      htmlEditor.callMethod('on', ['change', (a, b) {
        ctx._setValueInner((element as TextAreaElement).value);
      }]);
      refreshEditor();
    }
  }

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    var ctx = utils.getContext(scope, (_) => _ is InputHtmlComponent) as InputHtmlComponent;
    if (ctx != null) {
      addAttribute('maxlength', ctx.maxLength);
      addAttribute('placeholder', ctx.placeholder);
    }
  }

  void refreshEditor() {
    if (scope != null && scope.context is InputHtmlComponent) {
      var ctx = scope.context as InputHtmlComponent;
      htmlEditor.callMethod('setValue', [ctx.value != null ? ctx.value : '']);
      htmlEditor.callMethod('refresh');
    }
  }

  JsObject get htmlEditor => context['\$'].apply([element]).callMethod('data', ['htmleditor'])['editor'];
}