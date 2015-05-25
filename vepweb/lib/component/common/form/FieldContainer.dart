part of vep.component.common.form;

abstract class FieldContainer<A> {
  @NgTwoWay('errors')
  List<String> errors = [];

  A get formData;

  Map<String, FieldComponent> _fields = {};

  void addField(String name, FieldComponent fieldComponent) {
    _fields[name] = fieldComponent;
    var thisMirror = reflect(this);
    var classMirror = thisMirror.type;
    var fieldName = classMirror.declarations.keys.firstWhere((e) => MirrorSystem.getName(e) == name, orElse: () => null);
    if (fieldName != null){
      thisMirror.setField(fieldName, fieldComponent);
    } //else nothing
  }

  void setValue(String name, Object value) {
    var dataMirror = reflect(formData);
    var classMirror = dataMirror.type;
    var fieldName = classMirror.declarations.keys.firstWhere((e) => MirrorSystem.getName(e) == name, orElse: () => null);
    if (fieldName != null){
      dataMirror.setField(fieldName, value);
    } //else nothing
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
}