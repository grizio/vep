part of vep.component.main;

@Component(
    selector: 'navigation',
    templateUrl: '/packages/vepweb/component/main/navigation.html',
    useShadowDom: false)
class NavigationComponent {
  final App app;

  final leftLogout = <NavigationLink>[
    new NavigationLink('home', '/', 'Accueil'),
    new NavigationLink('register', '/user/register', 'Inscription'),
    new NavigationLink('login', '/login', 'Connexion')
  ];

  final leftLogin = <NavigationLink>[
    new NavigationLink('home', '/', 'Accueil'),
    new NavigationLink('logout', '/logout', 'Déconnnexion')
  ];

  bool loggedIn = false;

  NavigationComponent(this.app) {
    app.onBreadCrumbChange.listen((BreadCrumb breadCrumb){
      left.forEach((_) => _.active = false);
      if (breadCrumb.path.isNotEmpty) {
        var code = breadCrumb.path.first.code;
        var link = left.firstWhere((_) => _.code == code);
        if (link != null) {
          link.active = true;
        }
      }
    });

    app.onLogin.listen((_){
      loggedIn = true;
    });

    app.onLogout.listen((_){
      loggedIn = false;
    });
  }

  List<NavigationLink> get left => loggedIn ? leftLogin : leftLogout;
}

class NavigationLink {
  final String code;
  final String url;
  final String name;
  bool active = false;

  NavigationLink(this.code, this.url, this.name);
}