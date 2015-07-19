library vep.component.common.table;

import 'package:angular/angular.dart';
import 'package:klang/utilities/list.dart' as listUtilities;
import 'dart:math';
import 'dart:async';

part 'table-search.dart';

class VepComponentCommonTableModule extends Module {
  VepComponentCommonTableModule() {
    bind(TableSearchComponent);
  }
}