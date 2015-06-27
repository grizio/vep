part of vep.component.common.form;

class FormDataProxy {
  final Object innerObject;
  final FieldContainer fieldContainer;

  FormDataProxy(this.innerObject, this.fieldContainer);

  @override
  dynamic noSuchMethod(Invocation invocation) {
    InstanceMirror mirror = reflect(innerObject);
    if (invocation.isSetter) {
      var fieldName = MirrorSystem.getName(invocation.memberName);
      fieldName = fieldName.substring(0, fieldName.length - 1); //remove "=" of setter.
      var field = MirrorSystem.getSymbol(fieldName);

      if (mirror.type.declarations.containsKey(field)) {
        Object oldValue = mirror.getField(field).hasReflectee ? mirror.getField(field).reflectee : null;
        Object newValue = invocation.positionalArguments.first;
        if (oldValue != newValue) {
          var result = mirror.setField(field, newValue);
          fieldContainer.updateFieldFromModel(fieldName);
          return result;
        }
      }
    } else {
      return mirror.delegate(invocation);
    }
  }
}

abstract class FieldContainer<A> {
  @NgTwoWay('errors')
  List<String> errors = [];

  /// This attribute is used to list fields which need to be initialized.
  /// It bypass the limitation of angular which do not aware when child components are loaded.
  List<String> get fields;
  A _formData;
  A get formData => _formData;
  set formData(A value) {
    _formData = new FormDataProxy(value, this);
    var mirror = reflect(_formData);
    for (FieldComponent field in _fields.values) {
      field.value = mirror.getField(MirrorSystem.getSymbol(field.name));
    }
  }

  /// Used to initialize components.
  void initialize(){}

  Map<String, FieldComponent> _fields = {};
  List<String> _initializedFields = [];

  FieldComponent getField(String name) {
    return _fields[name];
  }

  void addField(String name, FieldComponent fieldComponent) {
    _fields[name] = fieldComponent;
    updateFieldFromModel(name);
    _initializedFields.add(name);
    if (fields.contains(name) && fields.every((_) => _initializedFields.contains(_))) {
      initialize();
    }
  }

  bool get isValid => _fields.values.every((_) => _.verify());

  void setErrors(HttpResultErrors httpResultErrors) {
    errors = [];
    for (var field in _fields.values) {
      field.errors = [];
    }
    var errorMessages = httpResultErrors.errorMessages;
    for (var key in errorMessages.keys) {
      if (_fields.containsKey(key)) {
        _fields[key].errors = errorMessages[key];
      } else {
        errors.addAll(errorMessages[key]);
      }
    }
  }

  void setError(HttpResultError httpResultError) {
    errors = [httpResultError.errorMessage];
  }

  void updateFieldFromModel(String field) {
    if (_fields.containsKey(field) && formData != null) {
      var valueMirror = reflect(formData).getField(MirrorSystem.getSymbol(field));
      _fields[field].forceSetValue(valueMirror.hasReflectee ? valueMirror.reflectee : null);
    }
  }

  void updateModelFromField(String field) {
    if (_fields.containsKey(field) && formData != null) {
      reflect(formData).setField(MirrorSystem.getSymbol(field), _fields[field].value);
    }
  }
}