part of vep.component.common.field;

/// This interface provides a simple attribute [pattern] used to add a [pattern] attribute in [input] component.
abstract class InputPatternComponent {
  @NgAttr('pattern')
  String pattern;
}

/// This class is the parent for all components using [input] HTML tag as text format.
/// It provides some common attributes copied from [input] HTML tag.
abstract class InputComponent<A> extends FieldComponent<A> {
  @NgOneWay('maxlength')
  int maxLength;

  @NgAttr('placeholder')
  String placeholder;

  InputElement input;

  @override
  bool verify() {
    super.verify();
    if (input != null && !input.checkValidity()) {
      errors.add(input.validationMessage);
    }
    return errors.isEmpty;
  }
}

/// This decorator is common for all [input] fields in this application.
/// It will be used to add attribute in HTML in terms of developer configuration.
abstract class InputDecorator extends FieldDecorator implements AttachAware {
  InputDecorator(Element element): super(element);

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    var ctx = utils.getContext(scope, (_) => _ is InputComponent) as InputComponent;
    if (ctx != null) {
      addAttribute('maxlength', ctx.maxLength);
      addAttribute('placeholder', ctx.placeholder);
      addAttribute('value', ctx.value);
      if (this is InputPatternComponent) {
        addAttribute('pattern', (ctx as InputPatternComponent).pattern);
      }
    }
  }

  @override
  void attach() {
    var ctx = utils.getContext(scope, (_) => _ is InputComponent) as InputComponent;
    if (ctx != null) {
      ctx.input = element;
      ctx.verify();
    }
  }
}