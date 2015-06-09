library vep.component.common;

import 'package:angular/angular.dart';

import 'field/vep.component.common.field.lib.dart';
import 'link/vep.component.common.link.lib.dart';
import 'misc/vep.component.common.misc.lib.dart';
import 'security/vep.component.common.security.lib.dart';
import 'table/vep.component.common.table.lib.dart';

class VepComponentCommonModule extends Module {
  VepComponentCommonModule() {
    install(new VepComponentCommonFieldsModule());
    install(new VepComponentCommonLinkModule());
    install(new VepComponentCommonMiscModule());
    install(new VepComponentCommonSecurityModule());
    install(new VepComponentCommonTableModule());
  }
}