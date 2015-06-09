library vep.component.common.link;

import 'dart:html';
import 'dart:math';
import 'package:angular/angular.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';

part 'AnchorDecorator.dart';
part 'pagination.dart';

class VepComponentCommonLinkModule extends Module {
  VepComponentCommonLinkModule() {
    bind(AnchorDecorator);
    bind(PaginationComponent);
  }
}