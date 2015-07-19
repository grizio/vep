part of vep.component.common.field;

@Component(
    selector: 'input-multiple-choice',
    templateUrl: '/packages/vepweb/component/common/field/input-multiple-choice.html',
    useShadowDom: false)
class InputMultipleChoiceComponent extends FieldComponent<List<A>> {
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

@Decorator(selector: '.input-multiple-choice')
class InputMultipleChoiceDecorator extends FieldDecorator {
  InputMultipleChoiceDecorator(Element element) : super(element);
}