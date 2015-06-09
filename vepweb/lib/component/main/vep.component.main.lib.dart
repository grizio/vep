library vep.component.main;

import 'dart:html';
import 'package:angular/angular.dart';
import 'package:vepweb/component/vep.component.lib.dart';
import 'package:vepweb/http/vep.http.lib.dart';
import 'dart:async';

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