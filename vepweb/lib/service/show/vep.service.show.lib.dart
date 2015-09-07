library vep.service.show;

import 'package:angular/angular.dart';
import 'package:vepweb/http/vep.http.lib.dart';
import 'dart:async';
import 'package:logging/logging.dart';
import 'package:vepweb/model/show/vep.model.show.lib.dart';
import 'package:jsonx/jsonx.dart' as jsonx;
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';

part 'ShowService.dart';

class VepServiceShowModule extends Module {
  VepServiceShowModule() {
    bind(ShowService);
  }
}