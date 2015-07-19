library vep.component.common.misc;

import 'package:angular/angular.dart';
import 'dart:async';
import 'package:klang/klang.dart';
import 'dart:html';
import 'package:google_maps/google_maps.dart';
import 'package:vepweb/utils.dart' as utils;

part 'map.dart';
part 'processing.dart';

class VepComponentCommonMiscModule extends Module {
  VepComponentCommonMiscModule() {
    bind(MapComponent);
    bind(MapDecorator);
    bind(ProcessingComponent);
  }
}