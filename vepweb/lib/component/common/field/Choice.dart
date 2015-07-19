part of vep.component.common.field;

class Choice<A> {
  final A value;
  final Object label;

  const Choice(this.value, this.label);

  bool operator ==(Choice c) => value == c.value;
}