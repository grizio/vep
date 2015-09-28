library vep.routing;

import 'package:angular/angular.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';
import 'package:vepweb/service/cms/vep.service.cms.lib.dart';
import 'package:vepweb/service/theater/vep.service.theater.lib.dart';
import 'package:vepweb/service/company/vep.service.company.lib.dart';

part 'cms.dart';
part 'company.dart';
part 'reservation.dart';
part 'session.dart';
part 'show.dart';
part 'theater.dart';
part 'user.dart';

@Injectable()
class VepRouter extends UserRouter with CmsRouter, TheaterRouter, CompanyRouter, ShowRouter, SessionRouter, ReservationRouter {
  final App app;
  final PageService pageService;
  final TheaterService theaterService;
  final CompanyService companyService;

  VepRouter(this.app, this.pageService, this.theaterService, this.companyService);

  call(Router router, RouteViewFactory views) {
    views.configure({
      'user': ngRoute(
          path: '/user',
          mount: userRoute),
      'cms': ngRoute(
          path: '/cms',
          mount: cmsRoute
      ),
      'theater': ngRoute(
          path: '/theater',
          mount: theaterRoute
      ),
      'company': ngRoute(
          path: '/compan',
          mount: companyRoute
      ),
      'show': ngRoute(
        path: '/show',
        mount: showRoute
      ),
      'session': ngRoute(
        path: '/session',
        mount: sessionRoute
      ),
      'reservation': ngRoute(
        path: '/reservation',
        mount: reservationRoute
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