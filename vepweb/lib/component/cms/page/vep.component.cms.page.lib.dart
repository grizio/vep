library vep.component.cms.page;

import 'dart:html';
import 'package:angular/angular.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';
import 'dart:async';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:klang/klang.dart';
import 'package:vepweb/service/cms/vep.service.cms.lib.dart';
import 'package:vepweb/component/common/table/vep.component.common.table.lib.dart';
import 'package:vepweb/utils.dart' as utils;
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'package:klang/utilities/string.dart' as stringUtilities;
import 'package:vepweb/component/main/vep.component.main.lib.dart';

part 'cms-form-page.dart';
part 'cms-page-list.dart';
part 'cms-page-read.dart';

class VepComponentCmsPageModule extends Module {
  VepComponentCmsPageModule() {
    bind(CmsFormPageComponent);
    bind(CmsPageListComponent);
    bind(CmsPageReadComponent);
  }
}