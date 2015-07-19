library vep.component.theater;

import 'package:angular/angular.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';
import 'package:vepweb/service/theater/vep.service.theater.lib.dart';
import 'package:klang/klang.dart';
import 'package:vepweb/component/common/table/vep.component.common.table.lib.dart';
import 'dart:async';
import 'package:vepweb/utils.dart' as utils;
import 'package:jsonx/jsonx.dart' as jsonx;
import 'dart:html';
import 'dart:math';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:klang/utilities/string.dart' as stringUtilities;
import 'package:vepweb/component/main/vep.component.main.lib.dart';
import 'package:vepweb/service/common/vep.service.common.lib.dart';

part 'form-theater.dart';
part 'input-theater.dart';
part 'theater-card.dart';
part 'theater-list.dart';
part 'theater-plan.dart';

class VepComponentTheaterModule extends Module {
  VepComponentTheaterModule() {
    bind(FormTheaterComponent);
    bind(InputTheaterComponent);
    bind(InputTheaterDecorator);
    bind(TheaterListComponent);
    bind(TheaterPlanComponent);
    bind(TheaterPlanMapDecorator);
    bind(TheaterCardComponent);
  }
}