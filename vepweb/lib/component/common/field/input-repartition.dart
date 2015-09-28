part of vep.component.common.field;

/// This component provides the user to split an amount between a set of amounts.
@Component(
    selector: 'input-repartition',
    templateUrl: '/packages/vepweb/component/common/field/input-repartition.html',
    useShadowDom: false)
class InputRepartitionComponent extends InputComponent<Map<Object, int>> {
  final InputRepartitionComponentMap innerValue = new InputRepartitionComponentMap();

  List<Choice<Object>> _choices;

  @NgOneWay('choices')
  List<Choice<Object>> get choices => _choices;

  set choices(List<Choice<Object>> choices) => _choices = choices;

  @NgOneWay('total')
  int total;

  int get sum => innerValue.sum;

  @override
  bool verify() {
    super.verify();
    if (innerValue.sum != total) {
      errors.add('Vous devez répartir exactement le total parmis les différents champs proposés.');
    }
    return errors.isEmpty;
  }

  InputRepartitionComponent() {
    innerValue.onChange.listen((InputRepartitionComponentMapOnChangeEvent event){
      final result = <Object, int>{};
      event.newValue.forEach((k, v){
        result[choices[k].value] = v;
      });
      value = result;
    });
  }
}

class InputRepartitionComponentMap {
  final onChange = new InputRepartitionComponentMapOnChangeSubscriber();

  var innerValue = {};

  operator [](Object key) => innerValue[key];

  operator []=(Object key, int value) {
    final newValue = {};
    final oldValue = innerValue;
    newValue.addAll(oldValue);
    newValue[key] = value;
    innerValue = newValue;
    final event = new InputRepartitionComponentMapOnChangeEvent(oldValue, newValue);
    onChange.process(event);
  }

  int get sum {
    if (innerValue.isEmpty) {
      return 0;
    } else {
      return innerValue.values.reduce((total, value) => total + value);
    }
  }
}

class InputRepartitionComponentMapOnChangeEvent {
  final Map<Object, int> oldValue;
  final Map<Object, int> newValue;
  bool get hasChanged => oldValue == null && newValue == null || oldValue == newValue;

  const InputRepartitionComponentMapOnChangeEvent(this.oldValue, this.newValue);
}

typedef void InputRepartitionComponentMapOnChange(InputRepartitionComponentMapOnChangeEvent event);

class InputRepartitionComponentMapOnChangeSubscriber {
  List<InputRepartitionComponentMapOnChange> _listeners = [];

  void process(InputRepartitionComponentMapOnChangeEvent event) {
    _listeners.forEach((listener) {
      listener(event);
    });
  }

  void listen(InputRepartitionComponentMapOnChange listener) {
    _listeners.add(listener);
  }
}