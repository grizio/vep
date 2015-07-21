library vep.utils;

import 'package:jsonx/jsonx.dart' as jsonx;
import 'package:angular/angular.dart';
import 'package:klang/utilities/string.dart' as stringUtilities;

Map<String, Object> objectToMap(Object data) {
  // TODO: how to convert properly an object into a map?
  return jsonx.decode(jsonx.encode(data)) as Map<String, Object>;
}

List<Map<String, Object>> objectToListOfMap(Object data) {
  return jsonx.decode(jsonx.encode(data)) as List<Map<String, Object>>;
}

/// This function is used to check the type of given object.
/// The returning value must be [true] if the object if the given type.
/// The usage of this function avoids Mirror.
/// Common use:
///
///     (obj) => obj is MyType
typedef bool checkType(Object o);

/// Gets the context of the scope of type checked by given [checker].
/// It is useful when components are included one into another
/// because it will check all parent scopes until it gets the wanted type (or no parent scope anymore).
Object getContext(Scope scope, checkType checker) {
  var currentScope = scope;
  while (currentScope != null) {
    if (currentScope.context != null && checker(currentScope.context)) {
      return currentScope.context;
    }
  }
  return null;
}

String canonicalize(String str) {
  // TODO: optimize
  if (stringUtilities.isBlank(str)) {
    return '';
  } else {
    str = str.toLowerCase();
    str = str.replaceAll(new RegExp('(à|ä|â|@)'), 'a');
    str = str.replaceAll(new RegExp('(é|è|ë|ê)'), 'e');
    str = str.replaceAll(new RegExp('(ï|î)'), 'i');
    str = str.replaceAll(new RegExp('(ö|ô)'), 'o');
    str = str.replaceAll(new RegExp('(ù|ü|û)'), 'u');
    str = str.replaceAll(new RegExp('\\s+'), '-');
    str = str.replaceAll(new RegExp('[^a-z0-9_+-]+'), '-');
    str = str.replaceAll(new RegExp('-+'), '-');
    while (str.startsWith(new RegExp('(_|\\+|-)'))) {
      str = str.substring(1);
    }
    while (str.endsWith('_') || str.endsWith('-') || str.endsWith('+')) {
      str = str.substring(0, str.length - 1);
    }
    return str;
  }
}