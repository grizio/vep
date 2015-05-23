part of vep.component.common.field;

abstract class InputPatternComponent {
  @NgTwoWay('pattern')
  String pattern;
}

abstract class InputComponent<A> extends FieldComponent<A> {
  @NgOneWay('maxlength')
  int maxLength;

  @NgAttr('placeholder')
  String placeholder;
}

abstract class InputDecorator extends FieldDecorator {
  InputDecorator(Element element): super(element);

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    if (scope.context is InputComponent) {
      var ctx = scope.context as InputComponent;
      addAttribute('maxlength', ctx.maxLength);
      addAttribute('placeholder', ctx.placeholder);
      addAttribute('value', ctx.value);
      if (this is InputPatternComponent) {
        addAttribute('pattern', (ctx as InputPatternComponent).pattern);
      }
    }
  }
}