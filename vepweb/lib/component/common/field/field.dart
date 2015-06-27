part of vep.component.common.field;

typedef bool Constraint<A>(A element);
typedef bool EnableWhen();

abstract class FieldComponent<A> implements ScopeAware, AttachAware {
  Scope scope;

  Option<FieldContainer> fieldContainer;

  List<Constraint<A>> constraints = [];
  List<String> constraintErrors = [];
  ValueChangeSubscriber<A> onValueChange = new ValueChangeSubscriber();

  @NgAttr('name')
  String name;

  @NgAttr('label')
  String label;

  @NgAttr('with-id')
  String id;

  @NgOneWay('required')
  bool required;

  @NgTwoWay('errors')
  List<String> errors;

  bool get enabled => enableWhen();

  EnableWhen enableWhen = () => true;

  A _value;

  @NgTwoWay('value')
  A get value => _value;

  set value(A newValue) {
    if (enabled) {
      forceSetValue(newValue);
    }
  }

  void forceSetValue(A newValue) {
    var oldValue = _value;
    if (oldValue != newValue) {
      _value = newValue;
      fieldContainer.forEach((FieldContainer fc) => fc.updateModelFromField(name));
      verify();
      onValueChange.process(new ValueChangeEvent(oldValue, newValue, isValid));
    }
  }

  bool get isValid => errors.isEmpty;

  @override
  void attach() {
    var parentScope = scope.parentScope;
    while (parentScope != null && !(parentScope.context is FieldContainer)) {
      parentScope = parentScope.parentScope;
    }
    fieldContainer = Some(parentScope != null ? parentScope.context : null);
    fieldContainer.forEach((FieldContainer fc) => fc.addField(name, this));
  }

  void addConstraint(Constraint<A> constraint, String constraintError) {
    constraints.add(constraint);
    constraintErrors.add(constraintError);
  }

  bool verify() {
    errors = <String>[];
    for (var i = 0, c = constraints.length ; i < c ; i++) {
      if (!constraints[i](value)) {
        errors.add(constraintErrors[i]);
      }
    }
    return errors.isEmpty;
  }
}

abstract class FieldDecorator implements ScopeAware {
  final Element element;
  Scope _scope;

  FieldDecorator(this.element);

  Scope get scope => _scope;

  @override
  set scope(Scope scope) {
    _scope = scope;
    includeAttributes(scope);
  }

  void includeAttributes(Scope scope) {
    if (scope.context is FieldComponent) {
      var ctx = scope.context as FieldComponent;
      addAttribute('name', ctx.name);
      addAttribute('id', ctx.id);
      if (!ctx.enabled) {
        element.attributes['disabled'] = 'disabled';
      }
      if (ctx.required != null && ctx.required) {
        element.attributes['required'] = 'required';
      }
    }
  }

  void addAttribute(String attribute, Object value) {
    if (value != null) {
      element.attributes[attribute] = value;
    }
  }
}