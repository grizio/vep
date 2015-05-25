library vep.component.user;

import 'dart:html';
import 'package:angular/angular.dart';
import 'package:klang/utilities/string.dart' as stringUtilities;
import 'package:vepweb/errors.dart' as errorCodes;
import 'package:vepweb/component/vep.component.lib.dart';
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';
import 'package:vepweb/service/user/vep.service.user.lib.dart';
import 'dart:async';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';

part 'form-user-login.dart';
part 'form-user-registration.dart';

class VepComponentUserModule extends Module {
  VepComponentUserModule() {
    bind(FormUserLoginComponent);
    bind(FormUserRegistrationComponent);
  }
}