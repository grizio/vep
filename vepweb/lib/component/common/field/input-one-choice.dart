part of vep.component.common.field;

/// Provides a way for the user to provides one value in terms of a list of choices.
@Component(
    selector: 'input-one-choice',
    templateUrl: '/packages/vepweb/component/common/field/input-one-choice.html',
    useShadowDom: false)
class InputOneChoiceComponent extends FieldComponent<Object> {
  @NgOneWayOneTime('explode')
  bool explode;

  @NgOneWay('choices')
  List<Choice<Object>> choices;

  List<InputElement> elements = [];

  int get innerValue => choices.indexOf(new Choice(value, ''));

  set innerValue(int newValue) {
    if (0 <= newValue && newValue < choices.length) {
      value = choices[newValue].value;
    } else {
      value = null;
    }
  }

  @override
  bool verify() {
    super.verify();
    if (required && innerValue < 0) {
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