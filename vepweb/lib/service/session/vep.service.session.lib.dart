library vep.service.session;

import 'package:angular/angular.dart';
import 'package:vepweb/http/vep.http.lib.dart';
import 'dart:async';
import 'package:logging/logging.dart';
import 'package:vepweb/model/vep.model.lib.dart';

part 'SessionService.dart';

class VepServiceSessionModule extends Module {
  VepServiceSessionModule() {
    bind(SessionService);
  }
}