library vep;

import 'package:angular/angular.dart';
import 'component/vep.component.lib.dart';
import 'http/vep.http.lib.dart';
import 'routing/vep.routing.lib.dart';
import 'service/vep.service.lib.dart';

class VepModule extends Module {
  VepModule() {
    install(new VepComponentModule());
    install(new VepHttpModule());
    install(new VepRoutingModule());
    install(new VepServiceModule());
  }
}