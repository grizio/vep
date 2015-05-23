library vep.component.main;

import 'package:angular/angular.dart';

part 'App.dart';

class VepComponentMainModule extends Module {
  VepComponentMainModule() {
    bind(App);
  }
}