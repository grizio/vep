library vep.component.main;

import 'dart:html';
import 'package:angular/angular.dart';
import 'package:vepweb/component/vep.component.lib.dart';
import 'package:vepweb/service/cms/vep.service.cms.lib.dart';
import 'dart:async';
import 'package:vepweb/model/vep.model.lib.dart';

part 'App.dart';
part 'breadcrumb.dart';
part 'navigation.dart';
part 'navigation-link.dart';

class VepComponentMainModule extends Module {
  VepComponentMainModule() {
    bind(App);
    bind(BreadCrumbComponent);
    bind(NavigationComponent);
    bind(NavigationLinkComponent);
  }
}