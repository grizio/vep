part of vep.component.common.form;

/// This class describes a component containing several fields.
abstract class FieldContainer {
  @NgTwoWay('errors')
  List<String> errors = [];

  List<FieldComponent> fields = [];
  List<String> _initializedFields = [];

  void addField(FieldComponent fieldComponent) {
    fields.add(fieldComponent);
    _initializedFields.add(fieldComponent.name);
  }

  bool get isValid => fields.every((_) => _.verify());

  void updateFromModel(Object data, String fieldName) {
    var field = fields.firstWhere((_) => _.name == fieldName, orElse: () => null);
    if (field != null) {
      var valueMirror = reflect(data).getField(MirrorSystem.getSymbol(fieldName));
      field.forceSetValue(valueMirror.hasReflectee ? valueMirror.reflectee : null);
    }
  }

  void updateToModel(Object data, String fieldName) {
    var field = fields.firstWhere((_) => _.name == fieldName, orElse: () => null);
    if (field != null) {
      reflect(data).setField(MirrorSystem.getSymbol(fieldName), field.value);
    }
  }

  void updateAllFromModel(Object data) {
    for (var field in fields) {
      updateFromModel(data, field.name);
    }
  }

  FieldComponent operator [](String name) {
    return fields.firstWhere((_) => _.name == name, orElse: () => null);
  }

  void propagateErrors(Map<String, List<String>> errors) {
    for (var field in fields) {
      if (errors.containsKey(field.name)) {
        field.errors = errors[field.name];
      } else {
        field.errors = [];
      }
    }
  }
}