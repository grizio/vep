library vep.component;

import 'package:angular/angular.dart';

import 'common/vep.component.common.lib.dart';
import 'company/vep.component.company.lib.dart';
import 'main/vep.component.main.lib.dart';
import 'user/vep.component.user.lib.dart';
import 'cms/vep.component.cms.lib.dart';
import 'theater/vep.component.theater.lib.dart';
import 'show/vep.component.show.lib.dart';

part 'Subscriber.dart';

class VepComponentModule extends Module {
  VepComponentModule() {
    install(new VepComponentCommonModule());
    install(new VepComponentCompanyModule());
    install(new VepComponentMainModule());
    install(new VepComponentUserModule());
    install(new VepComponentCmsModule());
    install(new VepComponentTheaterModule());
    install(new VepComponentShowModule());
  }
}