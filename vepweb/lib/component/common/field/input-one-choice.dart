part of vep.component.common.field;

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
    if (required && elements.every((_) => !_.checked)) {
      errors.add('Veuillez séléctionner au moins un élément.');
    }
    return errors.isEmpty;
  }
}

@Decorator(selector: '.input-one-choice')
class InputOneChoiceDecorator extends FieldDecorator implements AttachAware {
  InputOneChoiceDecorator(Element element) : super(element);

  @override
  void attach() {
    var ctx = utils.getContext(scope, InputOneChoiceComponent) as InputOneChoiceComponent;
    if (ctx != null) {
      ctx.elements.add(element);
    }
  }
}