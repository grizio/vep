library vep.service;

import 'package:angular/angular.dart';
import 'user/vep.service.user.lib.dart';

class VepServiceModule extends Module {
  VepServiceModule() {
    install(new VepServiceUserModule());
  }
}