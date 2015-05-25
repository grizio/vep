library vep.component.main;

import 'dart:html';
import 'package:angular/angular.dart';
import 'package:vepweb/component/vep.component.lib.dart';

part 'App.dart';
part 'breadcrumb.dart';
part 'navigation.dart';

class VepComponentMainModule extends Module {
  VepComponentMainModule() {
    bind(App);
    bind(BreadCrumbComponent);
    bind(NavigationComponent);
  }
}