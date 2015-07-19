library vep.service;

import 'package:angular/angular.dart';
import 'user/vep.service.user.lib.dart';
import 'cms/vep.service.cms.lib.dart';
import 'theater/vep.service.theater.lib.dart';

class VepServiceModule extends Module {
  VepServiceModule() {
    install(new VepServiceUserModule());
    install(new VepServiceCmsModule());
    install(new VepServiceTheaterModule());
  }
}