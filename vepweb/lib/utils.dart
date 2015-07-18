library vep.utils;

import 'package:jsonx/jsonx.dart' as jsonx;
import 'package:angular/angular.dart';
import 'dart:mirrors';

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