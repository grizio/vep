part of vep.component.main;

@Component(
    selector: 'navigation',
    templateUrl: '/packages/vepweb/component/main/navigation.html',
    useShadowDom: false)
class NavigationComponent {
  final App app;

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
  }

  List<NavigationLink> left = [
    new NavigationLink('home', '/', 'Accueil'),
    new NavigationLink('register', '/user/register', 'Inscription')
  ];
}

class NavigationLink {
  final String code;
  final String url;
  final String name;
  bool active = false;

  NavigationLink(this.code, this.url, this.name);
}