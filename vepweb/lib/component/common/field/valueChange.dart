part of vep.component.common.field;

typedef void OnValueChange<A>(ValueChangeEvent<A> event);

class ValueChangeEvent<A> {
  final A oldValue;
  final A newValue;
  final bool isValid;
  bool get hasChanged => oldValue == null && newValue == null || oldValue == newValue;

  const ValueChangeEvent(this.oldValue, this.newValue, this.isValid);
}

class ValueChangeSubscriber<A> {
  List<OnValueChange<A>> _listeners = [];

  void process(ValueChangeEvent<A> event) {
    _listeners.forEach((listener){
      listener(event);
    });
  }

  void listen(OnValueChange<A> listener) {
    _listeners.add(listener);
  }
}