part of vep.component.common.field;

/// Format of listener form value change event.
typedef void OnValueChange<A>(ValueChangeEvent<A> event);

/// This class describes all objects constructed when a value change event is triggered.
class ValueChangeEvent<A> {
  final A oldValue;
  final A newValue;
  final bool isValid;

  bool get hasChanged => oldValue == null && newValue == null || oldValue == newValue;

  const ValueChangeEvent(this.oldValue, this.newValue, this.isValid);
}

/// This class provides a simple way for components to add and trigger listeners.
class ValueChangeSubscriber<A> {
  List<OnValueChange<A>> _listeners = [];

  void process(ValueChangeEvent<A> event) {
    _listeners.forEach((listener) {
      listener(event);
    });
  }

  void listen(OnValueChange<A> listener) {
    _listeners.add(listener);
  }
}