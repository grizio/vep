library vep.service.common;

import 'package:angular/angular.dart';
import 'package:google_maps/google_maps.dart';
import 'dart:async';

part 'GeocodingService.dart';

class VepServiceCommonModule extends Module {
  VepServiceCommonModule() {
    bind(GeocodingService);
  }
}