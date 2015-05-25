library vep.routing;

import 'package:angular/angular.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';

part 'user.dart';

@Injectable()
class VepRouter extends UserRouter {
  final App app;

  VepRouter(this.app);

  call(Router router, RouteViewFactory views) {
    views.configure({
      'user': ngRoute(
          path: '/user',
          mount: userRoute),
      'login': ngRoute(
          path: '/login',
          view: '/public/views/user/login.html',
          preEnter: (_) => app.breadCrumb = new BreadCrumb().child('login', '/login', 'Connexion')
      ),
      'logout': ngRoute(
          path: '/logout',
          enter: (_) {
            app.logout();
            router.gotoUrl('/');
          }
      ),
      'home': ngRoute(
          path: '/',
          view: '/public/views/home.html',
          defaultRoute: true,
          preEnter: (_) => app.breadCrumb = new BreadCrumb().child('home', '/', 'Accueil')
      )
    });
  }
}

class VepRoutingModule extends Module {
  VepRoutingModule() {
    bind(RouteInitializerFn, toImplementation: VepRouter);
    bind(NgRoutingUsePushState, toValue: new NgRoutingUsePushState.value(true));
  }
}