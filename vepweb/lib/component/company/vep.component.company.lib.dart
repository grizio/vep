library vep.component.company;

import 'package:angular/angular.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';
import 'dart:async';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:klang/klang.dart';
import 'package:vepweb/component/common/table/vep.component.common.table.lib.dart';
import 'package:vepweb/utils.dart' as utils;
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'package:klang/utilities/string.dart' as stringUtilities;
import 'package:vepweb/component/main/vep.component.main.lib.dart';
import 'package:vepweb/service/company/vep.service.company.lib.dart';
import 'package:vepweb/service/common/vep.service.common.lib.dart';

part 'company-card.dart';
part 'company-list.dart';
part 'form-company.dart';

class VepComponentCompanyModule extends Module {
  VepComponentCompanyModule() {
    bind(CompanyCardComponent);
    bind(CompanyListComponent);
    bind(FormCompanyComponent);
  }
}