library vep.component;

import 'package:angular/angular.dart';

import 'common/vep.component.common.lib.dart';
import 'main/vep.component.main.lib.dart';
import 'user/vep.component.user.lib.dart';

part 'Subscriber.dart';

class VepComponentModule extends Module {
  VepComponentModule() {
    install(new VepComponentCommonModule());
    install(new VepComponentMainModule());
    install(new VepComponentUserModule());
  }
}