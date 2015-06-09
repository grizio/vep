library vep.component.common.misc;

import 'package:angular/angular.dart';
import 'dart:async';

part 'processing.dart';

class VepComponentCommonMiscModule extends Module {
  VepComponentCommonMiscModule() {
    bind(ProcessingComponent);
  }
}