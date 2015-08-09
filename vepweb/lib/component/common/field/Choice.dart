part of vep.component.common.field;

/// This class describes an element used by all fields using a [List<Choice>] as possible values.
class Choice<A> {
  final A value;
  final Object label;

  const Choice(this.value, this.label);

  bool operator ==(Choice c) => value == c.value;

  String toString() {
    return '(${value.toString()} => ${label.toString()})';
  }
}