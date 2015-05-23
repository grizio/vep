library vep.routing;

import 'package:angular/angular.dart';

part 'user.dart';

void routingInitializer(Router router, RouteViewFactory views) {
  views.configure({
    'user': ngRoute(
        path: '/user',
        mount: userRoute),
    'home': ngRoute(
        path: '/',
        view: '/public/views/home.html',
        defaultRoute: true)
  });
}

class VepRoutingModule extends Module {
  VepRoutingModule() {
    bind(RouteInitializerFn, toValue: routingInitializer);
    bind(NgRoutingUsePushState, toValue: new NgRoutingUsePushState.value(true));
  }
}