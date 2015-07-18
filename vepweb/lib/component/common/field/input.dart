part of vep.component.common.field;

abstract class InputPatternComponent {
  @NgAttr('pattern')
  String pattern;
}

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

abstract class InputDecorator extends FieldDecorator implements AttachAware {
  InputDecorator(Element element): super(element);

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    var ctx = utils.getContext(scope, InputComponent) as InputComponent;
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
    var ctx = utils.getContext(scope, InputComponent) as InputComponent;
    if (ctx != null) {
      ctx.input = element;
    }
  }
}