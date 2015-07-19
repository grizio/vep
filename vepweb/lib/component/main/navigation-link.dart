part of vep.component.main;

@Component(
    selector: 'navigation-link',
    templateUrl: '/packages/vepweb/component/main/navigation-link.html',
    useShadowDom: false)
class NavigationLinkComponent implements ScopeAware, AttachAware {
  static const activeNavigationLink = 'active-navigation-link';

  final App app;

  @override
  Scope scope;

  @NgAttr('code')
  String code;

  @NgOneWayOneTime('name')
  String name;

  @NgOneWayOneTime('url')
  String url;

  @NgAttr('role')
  String role;

  @NgOneWay('has-children')
  bool hasChildren;

  @NgAttr('parent')
  String parent;

  bool _active = false;

  bool get active => _active;

  set active(bool value) {
    if (parent != null && value) {
      scope.parentScope.broadcast(activeNavigationLink, parent);
    }
    _active = value;
  }

  NavigationLinkComponent(this.app) {
    app.onBreadCrumbChange.listen((BreadCrumb breadCrumb) {
      updateActive(breadCrumb);
    });
  }

  @override
  void attach() {
    scope.on(activeNavigationLink).listen((event) {
      if (event.data == code) {
        active = true;
      }
    });
    updateActive(app.breadCrumb);
  }

  updateActive(BreadCrumb breadCrumb) {
    if (breadCrumb != null) {
      if (breadCrumb.path.isEmpty) {
        active = code == 'home';
      } else {
        active = breadCrumb.path.any((_) => _.code == code);
      }
    }
  }

  bool get isGranted => role == null || role.split('|').any((_) => _.split('&').every((_) => app.roles.contains(_)));
}