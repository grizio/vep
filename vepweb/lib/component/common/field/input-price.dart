part of vep.component.common.field;

/// This component provides the user to enter an integer value.
@Component(
    selector: 'input-price',
    templateUrl: '/packages/vepweb/component/common/field/input-price.html',
    useShadowDom: false)
class InputPriceComponent extends InputComponent<double> {
  @NgOneWay('min')
  double min;

  @NgOneWay('max')
  double max;

  double _step = 0.05;

  @NgOneWay('step')
  double get step => _step;

  set step(double step) {
    if ((step / 0.01).floorToDouble() != step) {
      throw '$step is not an acceptable step for a price value.';
    }
    _step = step;
  }

  @NgOneWay('convert-to-cent')
  bool convertToCent = false;

  double get innerValue {
    if (value == null) {
      return null;
    } else {
      return convertToCent ? value / 100 : value;
    }
  }

  set innerValue(double innerValue) {
    if (innerValue == null) {
      value = null;
    } else {
      value = convertToCent ? innerValue * 100 : innerValue;
    }
  }
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: 'input.input-price')
class InputPriceDecorator extends InputDecorator {
  InputPriceDecorator(Element element) : super(element);

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    var ctx = utils.getContext(scope, (_) => _ is InputPriceComponent) as InputPriceComponent;
    if (ctx != null) {
      addAttribute('min', ctx.min.toString());
      addAttribute('max', ctx.max.toString());
      addAttribute('step', ctx.step.toString());
    }
  }
}