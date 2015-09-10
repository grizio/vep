part of vep.component.common.form;

/// This class describes a component containing several fields.
abstract class FieldContainer {
  @NgTwoWay('errors')
  List<String> errors = [];

  List<FieldComponent> fields = [];
  List<String> _initializedFields = [];

  //copied to RepeatableContainer
  //update it when updating here
  void addField(FieldComponent fieldComponent) {
    fields.add(fieldComponent);
    _initializedFields.add(fieldComponent.name);
  }

  bool get isValid => fields.every((_) => _.verify());

  void updateFromModel(Object data, String fieldName) {
    var field = fields.firstWhere((_) => _.name == fieldName, orElse: () => null);
    if (field != null) {
      var valueMirror = reflect(data).getField(MirrorSystem.getSymbol(fieldName));
      var value = valueMirror.hasReflectee ? valueMirror.reflectee : null;
      field.forceSetValue(value);
      if (field is FieldContainer) {
        (field as FieldContainer).updateAllFromModel(data);
      }
    }
  }

  void updateToModel(Object data, String fieldName) {
    var field = fields.firstWhere((_) => _.name == fieldName, orElse: () => null);
    if (field != null) {
      reflect(data).setField(MirrorSystem.getSymbol(fieldName), field.value);
    }
  }

  //copied to RepeatableContainer
  //update it when updating here
  void updateAllFromModel(Object data) {
    for (var field in fields) {
      updateFromModel(data, field.name);
    }
  }

  //copied to RepeatableContainer
  //update it when updating here
  FieldComponent operator [](String name) {
    return fields.firstWhere((_) => _.name == name, orElse: () => null);
  }

  //copied to RepeatableContainer
  //update it when updating here
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