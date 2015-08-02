library vep.service;

import 'package:angular/angular.dart';
import 'cms/vep.service.cms.lib.dart';
import 'common/vep.service.common.lib.dart';
import 'company/vep.service.company.lib.dart';
import 'theater/vep.service.theater.lib.dart';
import 'user/vep.service.user.lib.dart';

class VepServiceModule extends Module {
  VepServiceModule() {
    install(new VepServiceCmsModule());
    install(new VepServiceCommonModule());
    install(new VepServiceCompanyModule());
    install(new VepServiceTheaterModule());
    install(new VepServiceUserModule());
  }
}