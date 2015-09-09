library vep.component.common.table;

import 'package:angular/angular.dart';
import 'package:klang/utilities/list.dart' as listUtilities;
import 'dart:math';
import 'dart:async';
import 'package:vepweb/component/common/field/vep.component.common.field.lib.dart';
import 'dart:html';
import 'dart:js';
import 'package:vepweb/component/common/date-constants.dart' as dateConstants;
import 'package:intl/intl.dart';
import 'package:vepweb/utils.dart' as utils;

part 'table-search.dart';

class VepComponentCommonTableModule extends Module {
  VepComponentCommonTableModule() {
    bind(TableSearchComponent);
    bind(TableSearchDecorator);
    bind(TableSearchCriteriaDateDecorator);
  }
}