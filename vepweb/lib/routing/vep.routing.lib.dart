library vep.routing;

import 'package:angular/angular.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';
import 'package:vepweb/service/cms/vep.service.cms.lib.dart';

part 'cms.dart';
part 'user.dart';

@Injectable()
class VepRouter extends UserRouter with CmsRouter {
  final App app;
  final PageService pageService;

  VepRouter(this.app, this.pageService);

  call(Router router, RouteViewFactory views) {
    views.configure({
      'user': ngRoute(
          path: '/user',
          mount: userRoute),
      'cms': ngRoute(
          path: '/cms',
          mount: cmsRoute
      ),
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
      ),
      'page-read': ngRoute(
          path: '/page/:canonical',
          view: '/public/views/cms/page/read.html',
          preEnter: (_) => app.breadCrumb = new BreadCrumb().child('cms-page-read', '/page/' + _.parameters['canonical'], '')
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