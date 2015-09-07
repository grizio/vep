library vep.model;

export 'user/vep.model.user.lib.dart';
export 'cms/vep.model.cms.lib.dart';
export 'theater/vep.model.theater.lib.dart';
export 'company/vep.model.company.lib.dart';
export 'show/vep.model.show.lib.dart' hide prepareJsonx;
export 'session/vep.model.session.lib.dart';

import 'show/vep.model.show.lib.dart' as show;
import 'dart:mirrors';

prepareJsonx() {
  show.prepareJsonx();
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