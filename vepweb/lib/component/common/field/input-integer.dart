part of vep.component.common.field;

@Component(
    selector: 'input-integer',
    templateUrl: '/packages/vepweb/component/common/field/input-integer.html',
    useShadowDom: false)
class InputIntegerComponent extends InputComponent<int> {
  @NgOneWay('min')
  int min;

  @NgOneWay('max')
  int max;

  @NgOneWay('step')
  int step;
}

@Decorator(selector: 'input.input-integer')
class InputIntegerDecorator extends InputDecorator {
  InputIntegerDecorator(Element element) : super(element);

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    if (scope.context is InputIntegerComponent) {
      var ctx = scope.context as InputIntegerComponent;
      addAttribute('min', ctx.min);
      addAttribute('max', ctx.max);
      addAttribute('step', ctx.step);
    }
  }
}