library vep.component.cms;

import 'package:angular/angular.dart';
import 'page/vep.component.cms.page.lib.dart';

class VepComponentCmsModule extends Module {
  VepComponentCmsModule() {
    install(new VepComponentCmsPageModule());
  }
}