part of vep.component.common.field;

/// This component provides the user to enter an integer value.
@Component(
    selector: 'input-integer',
    templateUrl: '/packages/vepweb/component/common/field/input-integer.html',
    useShadowDom: false)
class InputIntegerComponent extends InputComponent<int> {
  InputIntegerDecorator decorator;

  int _min;
  int _max;
  int _step = 1;

  @NgOneWay('min')
  int get min => _min;
  set min(int min) {
    _min = min;
    if (decorator != null) {
      if (min == null) {
        decorator.element.attributes.remove('min');
      } else {
        decorator.element.attributes['min'] = min.toString();
      }
    }
  }

  @NgOneWay('max')
  int get max => _max;
  set max(int max) {
    _max = max;
    if (decorator != null) {
      if (max == null) {
        decorator.element.attributes.remove('max');
      } else {
        decorator.element.attributes['max'] = max.toString();
      }
    }
  }

  @NgOneWay('step')
  int get step => _step;
  set step(int step) {
    _step = step;
    if (decorator != null) {
      if (step== null) {
        decorator.element.attributes.remove('step');
      } else {
        decorator.element.attributes['step'] = step.toString();
      }
    }
  }
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: 'input.input-integer')
class InputIntegerDecorator extends InputDecorator {
  InputIntegerDecorator(Element element) : super(element);

  @override
  set scope(Scope scope) {
    super.scope = scope;
    var ctx = utils.getContext(scope, (_) => _ is InputIntegerComponent) as InputIntegerComponent;
    if (ctx != null) {
      ctx.decorator = this;
    }
  }

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