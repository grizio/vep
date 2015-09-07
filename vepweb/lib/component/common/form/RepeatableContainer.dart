part of vep.component.common.form;

/// This class describes a component containing several fields.
abstract class RepeatableContainer<A> extends FieldComponent<List<A>> with FieldContainer {
  @NgOneWay('min')
  int min;

  @NgOneWay('max')
  int max;

  @NgOneWayOneTime('initial')
  int initial;

  @NgOneWayOneTime('model')
  A createModel();

  int current = 0;

  List<int> indexList() => new List.generate(current, (_) => _);

  @override
  void attach() {
    super.attach();
    // put "initial ?? min ?? 1" when valid for IDE
    final toCreate = initial != null ? initial : min != null ? min : 1;
    // put "value ?? []" when valid for IDE
    value = value != null ? value : [];

    current = 0;
    for (int i = 0; i < toCreate; i++) {
      add();
    }
  }

  @override
  void addField(FieldComponent fieldComponent) {
    if (fieldComponent == this) {
    } else {
      super.addField(fieldComponent);
    }
  }

  @override
  void updateFromModel(Object data, String fieldName) {
    if (fieldName == name) {
      updateCurrentFromModel(data, fieldName);
    } else {
      updateInternalFromModel(data, fieldName);
    }
  }

  @override
  void updateToModel(Object data, String fieldName) {
    if (fieldName == name) {
      updateCurrentToModel(data, fieldName);
    } else {
      updateInternalToModel(data, fieldName);
    }
  }

  void updateCurrentFromModel(Object data, String fieldName) {
    var valueMirror = reflect(data).getField(MirrorSystem.getSymbol(fieldName));
    forceSetValue(valueMirror.hasReflectee ? valueMirror.reflectee : null);
  }

  void updateCurrentToModel(Object data, String fieldName) {
    reflect(data).setField(MirrorSystem.getSymbol(fieldName), value);
  }

  void updateInternalFromModel(Object data, String fieldName) {
    final field = fields.firstWhere((_) => _.name == fieldName, orElse: () => null);
    if (field != null) {
      final internalData = reflect(data).getField(MirrorSystem.getSymbol(name));
      if (internalData.hasReflectee && internalData.reflectee != null) {
        final list = internalData.reflectee as List<A>;
        final rgx = new RegExp('^([^\\]]+)\\[([0-9]+)\\]\$');
        final match = rgx.firstMatch(fieldName);
        if (match != null) {
          final valueMirror = reflect(list[int.parse(match.group(2))]).getField(MirrorSystem.getSymbol(match.group(1)));
          field.forceSetValue(valueMirror.hasReflectee ? valueMirror.reflectee : null);
        }
      }
    }
  }

  void updateInternalToModel(Object data, String fieldName) {
    final field = fields.firstWhere((_) => _.name == fieldName, orElse: () => null);
    if (field != null) {
      final internalData = reflect(data).getField(MirrorSystem.getSymbol(name));
      if (internalData.hasReflectee && internalData.reflectee != null) {
        final list = internalData.reflectee as List<A>;
        final rgx = new RegExp('^([^\\]]+)\\[([0-9]+)\\]\$');
        final match = rgx.firstMatch(fieldName);
        if (match != null) {
          reflect(list[int.parse(match.group(2))]).setField(MirrorSystem.getSymbol(match.group(1)), field.value);
        }
      }
    }
  }

  @override
  bool get isValid => errors.isEmpty && fields.every((_) => _.isValid);

  void add([Event event]) {
    if (event != null) {
      event.preventDefault();
    }
    var newValue = [];
    newValue.addAll(value);
    newValue.add(createModel());
    this.value = newValue;
    current++;
    reload();
  }

  void remove(int index, [Event event]) {
    if (event != null) {
      event.preventDefault();
    }
    var newValue = [];
    newValue.addAll(value);
    newValue.removeAt(index);
    this.value = newValue;
    current--;
    fields.removeWhere((_) => _.name.contains('[$current]'));
    reload();
  }

  void reload() {
    updateAllFromModel(formComponent.data);
  }
}