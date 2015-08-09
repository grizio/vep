part of vep.component.common.field;

/// Provides a way for the user to provides one value in terms of a list of choices.
@Component(
    selector: 'input-one-choice',
    templateUrl: '/packages/vepweb/component/common/field/input-one-choice.html',
    useShadowDom: false)
class InputOneChoiceComponent extends FieldComponent<Object> {
  @NgOneWayOneTime('explode')
  bool explode;

  List<Choice<Object>> _choices;

  @NgOneWay('choices')
  List<Choice<Object>> get choices => _choices;

  set choices(List<Choice<Object>> choices) {
    _choices = choices;
    verify();
  }

  List<InputElement> elements = [];

  String get innerValue => choices.indexOf(new Choice(value, '')).toString();

  set innerValue(String newValueStr) {
    int newValue = int.parse(newValueStr);
    if (0 <= newValue && newValue < choices.length) {
      value = choices[newValue].value;
    } else {
      value = null;
    }
  }

  @override
  bool verify() {
    super.verify();
    if (required && int.parse(innerValue) < 0) {
      errors.add('Veuillez sélectionner au moins un élément.');
    }
    return errors.isEmpty;
  }
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: '.input-one-choice')
class InputOneChoiceDecorator extends FieldDecorator implements AttachAware {
  InputOneChoiceDecorator(Element element) : super(element);

  @override
  void attach() {
    var ctx = utils.getContext(scope, (_) => _ is InputOneChoiceComponent) as InputOneChoiceComponent;
    if (ctx != null) {
      ctx.elements.add(element);
    }
  }
}