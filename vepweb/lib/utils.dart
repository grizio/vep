library vep.utils;

import 'package:jsonx/jsonx.dart' as jsonx;

// TODO: how to convert properly an object into a map?

Map<String, Object> objectToMap(Object data) {
  return jsonx.decode(jsonx.encode(data)) as Map<String, Object>;
}

List<Map<String, Object>> objectToListOfMap(Object data) {
  return jsonx.decode(jsonx.encode(data)) as List<Map<String, Object>>;
}