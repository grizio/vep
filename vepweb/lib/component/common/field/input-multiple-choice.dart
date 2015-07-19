part of vep.component.common.field;

/// Provides a way for the user to provides several values in terns of a list of choices.
@Component(
    selector: 'input-multiple-choice',
    templateUrl: '/packages/vepweb/component/common/field/input-multiple-choice.html',
    useShadowDom: false)
class InputMultipleChoiceComponent extends FieldComponent<List<Object>> {
  // TODO: check this component with concrete case.

  @NgOneWayOneTime('explode')
  bool explode;

  @NgTwoWay('choices')
  List<Choice<Object>> choices;

  List<int> get innerValue => value.map((_) => choices.indexOf(_));

  set innerValue(List<int> newValue) {
    var values = new Set();
    for (int index in newValue) {
      if (0 <= index && index < choices.length) {
        values.add(choices[index].value);
      } else {
        values.add(null);
      }
    }
    value = values.toList();
  }
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: '.input-multiple-choice')
class InputMultipleChoiceDecorator extends FieldDecorator {
  InputMultipleChoiceDecorator(Element element) : super(element);
}