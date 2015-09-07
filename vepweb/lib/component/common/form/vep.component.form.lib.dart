library vep.component.common.form;

import 'dart:mirrors';
import 'package:angular/angular.dart';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'dart:async';
import 'dart:html';
import 'package:vepweb/utils.dart' as utils;

part 'FieldContainer.dart';
part 'form-sections.dart';
part 'form-section.dart';
part 'form-simple.dart';
part 'form-step.dart';
part 'form-steps.dart';
part 'FormComponent.dart';
part 'RepeatableContainer.dart';

class VepComponentCommonFormModule extends Module {
  VepComponentCommonFormModule() {
    bind(FormSectionComponent);
    bind(FormSectionsComponent);
    bind(FormSimpleComponent);
    bind(FormStepComponent);
    bind(FormStepsComponent);
    bind(FormStepsDecorator);
  }
}