library vep.component.common.security;

import 'package:angular/angular.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';

part 'authenticate.dart';

class VepComponentCommonSecurityModule extends Module {
  VepComponentCommonSecurityModule() {
    bind(Authenticate);
  }
}