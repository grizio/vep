library vep.model;

export 'user/vep.model.user.lib.dart';
export 'cms/vep.model.cms.lib.dart';
export 'theater/vep.model.theater.lib.dart';
export 'company/vep.model.company.lib.dart';
export 'show/vep.model.show.lib.dart' hide prepareJsonx;
export 'session/vep.model.session.lib.dart';

import 'show/vep.model.show.lib.dart' as show;
import 'session/vep.model.session.lib.dart' as session;
import 'dart:mirrors';

prepareJsonx() {
  show.prepareJsonx();
  session.prepareJsonx();
}

class ModelToString {
  @override
  String toString() {
    var result = <String, String>{};
    final that = reflect(this);
    for (final element in that.type.declarations.values) {
      if (element is VariableMirror) {
        final variableMirror = element as VariableMirror;
        final name = MirrorSystem.getName(variableMirror.simpleName);
        final valueMirror = that.getField(variableMirror.simpleName);
        final value = valueMirror.hasReflectee ? valueMirror.reflectee.toString() : 'null';
        result[name] = value;
      }
    }
    return result.toString();
  }
}

void copyByAccessors(Object source, Object destination) {
  final destinationMirror = reflect(destination);
  final sourceMirror = reflect(source);
  for (final name in getFieldsByAccessors(source, destination)) {
    final field = MirrorSystem.getSymbol(name);
    final valueMirror = sourceMirror.getField(field);
    final value = valueMirror.hasReflectee ? valueMirror.reflectee : null;
    destinationMirror.setField(field, value);
  }
}

List<String> getFieldsByAccessors(Object source, Object destination) {
  final sourceMembers = reflect(source).type.instanceMembers;
  final getters = sourceMembers.keys
  .where((Symbol symbol) => sourceMembers[symbol].isGetter)
  .map((_) => MirrorSystem.getName(_)).toList();

  final destinationMembers = reflect(destination).type.instanceMembers;
  final setters = destinationMembers.keys
  .where((Symbol symbol) => destinationMembers[symbol].isSetter)
  .map((_) {
    final name = MirrorSystem.getName(_);
    return name.substring(0, name.length - 1);
  });

  return getters.where((_) => setters.contains(_)).toList();
}