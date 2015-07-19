library vep.utils;

import 'package:jsonx/jsonx.dart' as jsonx;
import 'package:angular/angular.dart';
import 'dart:mirrors';
import 'package:klang/utilities/string.dart' as stringUtilities;

// TODO: how to convert properly an object into a map?

Map<String, Object> objectToMap(Object data) {
  return jsonx.decode(jsonx.encode(data)) as Map<String, Object>;
}

List<Map<String, Object>> objectToListOfMap(Object data) {
  return jsonx.decode(jsonx.encode(data)) as List<Map<String, Object>>;
}

Object getContext(Scope scope, Type contextType) {
  var currentScope = scope;
  while (currentScope != null && !reflect(currentScope.context).type.isAssignableTo(reflectType(contextType))) {
    currentScope = currentScope.parentScope;
  }
  return currentScope != null ? currentScope.context : null;
}

// TODO: optimize
String canonicalize(String str) {
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