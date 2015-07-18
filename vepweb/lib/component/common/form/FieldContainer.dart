part of vep.component.common.form;

class FormDataProxy {
  final Object innerObject;
  final FormComponent formComponent;

  FormDataProxy(this.innerObject, this.formComponent);

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
          formComponent.updateFieldFromModel(fieldName);
          return result;
        }
      }
    } else {
      return mirror.delegate(invocation);
    }
  }
}

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

  FieldComponent operator[](String name) {
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