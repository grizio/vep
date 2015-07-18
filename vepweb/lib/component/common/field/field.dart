part of vep.component.common.field;

typedef bool Constraint<A>(A element);

typedef bool EnableWhen();

abstract class FieldComponent<A> implements ScopeAware, AttachAware {
  FormComponent formComponent;
  FieldContainer fieldContainer;

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

  @NgAttr('parent')
  String parent;

  Scope _scope;
  Scope get scope => _scope;
  set scope(Scope scope) {
    _scope = scope;
    var ctx = utils.getContext(scope, FormComponentContainer) as FormComponentContainer;
    if (ctx != null) {
      formComponent = ctx.form;
      formComponent.waitingField();
    }
  }

  @override
  void attach() {
    if (formComponent != null) {
      if (formComponent is FormSimpleComponent) {
        fieldContainer = formComponent as FieldContainer;
      } else {
        if (parent != null) {
          if (formComponent is FormSectionsComponent) {
            fieldContainer = (formComponent as FormSectionsComponent)[parent];
          } else if (formComponent is FormStepsComponent) {
            fieldContainer = (formComponent as FormStepsComponent)[parent];
          }
        }
      }

      if (fieldContainer != null) {
        fieldContainer.fields.add(this);
      }

      formComponent.fieldInitialized();
    }
  }

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
      if (fieldContainer != null) {
        fieldContainer.updateToModel(formComponent.data, name);
      }
      verify();
      onValueChange.process(new ValueChangeEvent(oldValue, newValue, isValid));
    }
  }

  bool get isValid => errors.isEmpty;

  void addConstraint(Constraint<A> constraint, String constraintError) {
    constraints.add(constraint);
    constraintErrors.add(constraintError);
  }

  bool verify() {
    errors = <String>[];
    for (var i = 0, c = constraints.length; i < c; i++) {
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
    var ctx = utils.getContext(scope, FieldComponent) as FieldComponent;
    if (ctx != null) {
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