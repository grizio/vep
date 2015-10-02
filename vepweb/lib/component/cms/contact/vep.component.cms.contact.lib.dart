library vep.component.cms.contact;

import 'package:angular/angular.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';
import 'dart:async';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'package:klang/utilities/string.dart' as stringUtilities;
import 'package:vepweb/errors.dart' as errorCodes;
import 'package:vepweb/service/cms/vep.service.cms.lib.dart';

part 'form-contact.dart';

class VepComponentCmsContactModule extends Module {
  VepComponentCmsContactModule() {
    bind(FormContactComponent);
  }
}