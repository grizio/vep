library vep.component.cms;

import 'package:angular/angular.dart';
import 'page/vep.component.cms.page.lib.dart';
import 'contact/vep.component.cms.contact.lib.dart';

class VepComponentCmsModule extends Module {
  VepComponentCmsModule() {
    install(new VepComponentCmsPageModule());
    install(new VepComponentCmsContactModule());
  }
}