library vep.component.show;

import 'package:angular/angular.dart';
import 'dart:async';
import 'package:klang/klang.dart';
import 'package:vepweb/component/common/table/vep.component.common.table.lib.dart';
import 'package:vepweb/utils.dart' as utils;
import 'package:vepweb/service/show/vep.service.show.lib.dart';
import 'package:vepweb/model/show/vep.model.show.lib.dart';
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'package:vepweb/service/company/vep.service.company.lib.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';
import 'package:vepweb/roles.dart' as roles;
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:klang/utilities/string.dart' as stringUtilities;
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';

part 'form-show.dart';
part 'show-card.dart';
part 'show-list.dart';

class VepComponentShowModule extends Module {
  VepComponentShowModule() {
    bind(FormShowComponent);
    bind(ShowCardComponent);
    bind(ShowListComponent);
  }
}