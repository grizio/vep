library vep.component.common;

import 'package:angular/angular.dart';

import 'field/vep.component.common.field.lib.dart';
import 'link/vep.component.common.link.lib.dart';

class VepComponentCommonModule extends Module {
  VepComponentCommonModule() {
    install(new VepComponentCommonFieldsModule());
    install(new VepComponentCommonLinkModule());
  }
}