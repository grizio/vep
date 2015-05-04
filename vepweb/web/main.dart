import 'dart:html';

import 'package:angular/angular.dart';
import 'package:angular/application_factory.dart';
import 'package:logging/logging.dart';

import 'package:vepweb/constants.dart' as constants;

void routingInitializer(Router router, RouteViewFactory views) {
  views.configure({
    //TODO: add routes
  });
}

class VepModule extends Module {
  VepModule() {
    bind(RouteInitializerFn, toValue: routingInitializer);
    bind(NgRoutingUsePushState, toValue: new NgRoutingUsePushState.value(true));

    //TODO: bind components
  }
}


void main() {
  Logger.root.level = constants.loggerLevel;
  Logger.root.onRecord.listen((LogRecord r) {
    print("[${r.level}][${r.loggerName}][${r.time.toIso8601String()}]${r.message}");
  });

  applicationFactory().addModule(new VepModule()).run();
}
