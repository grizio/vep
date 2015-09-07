part of vep.component.common.field;

/// This component provides the user to enter an integer value.
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
  int step = 1;
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: 'input.input-integer')
class InputIntegerDecorator extends InputDecorator {
  InputIntegerDecorator(Element element) : super(element);

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    var ctx = utils.getContext(scope, (_) => _ is InputIntegerComponent) as InputIntegerComponent;
    if (ctx != null) {
      addAttribute('min', ctx.min.toString());
      addAttribute('max', ctx.max.toString());
      addAttribute('step', ctx.step.toString());
    }
  }
}