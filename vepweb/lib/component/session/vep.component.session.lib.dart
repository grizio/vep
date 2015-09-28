library vep.component.session;

import 'package:angular/angular.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';
import 'dart:async';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/service/show/vep.service.show.lib.dart';
import 'package:vepweb/service/theater/vep.service.theater.lib.dart';
import 'package:vepweb/service/session/vep.service.session.lib.dart';
import 'package:vepweb/component/common/table/vep.component.common.table.lib.dart';
import 'package:vepweb/roles.dart' as roles;
import 'package:vepweb/utils.dart' as utils;

part 'field-price.dart';
part 'form-reservation.dart';
part 'form-session.dart';
part 'form-session-update.dart';
part 'price-card.dart';
part 'session-list.dart';
part 'session-card.dart';
part 'session-card-reservations.dart';

class VepComponentSessionModule extends Module {
  VepComponentSessionModule() {
    bind(FieldPriceComponent);
    bind(FormReservationComponent);
    bind(FormSessionComponent);
    bind(FormSessionUpdateComponent);
    bind(PriceCardComponent);
    bind(SessionListComponent);
    bind(SessionCardComponent);
    bind(SessionCardReservationComponent);
  }
}