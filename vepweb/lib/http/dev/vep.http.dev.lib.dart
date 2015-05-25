library vep.http.dev;

import 'package:angular/angular.dart';
import 'package:http/http.dart' as http;
import 'package:http/testing.dart';
import 'package:jsonx/jsonx.dart' as jsonx;
import 'package:vepweb/model/vep.model.lib.dart';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/errors.dart' as errors;

part 'ClientInMemory.dart';
part 'ClientInMemoryUser.dart';

const malformedRequestContent = "The request content was malformed: Object is missing required member 'xxx'";

class ErrorsManager {
  var errors = <String, List<int>>{};
  void add(String f, int c) {
    errors.putIfAbsent(f, () => <int>[]);
    errors[f].add(c);
  }

  bool get hasError => errors.isNotEmpty;

  String get toJson => jsonx.encode(errors);
}