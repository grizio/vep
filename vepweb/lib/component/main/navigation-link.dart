part of vep.component.main;

@Component(
    selector: 'navigation-link',
    templateUrl: '/packages/vepweb/component/main/navigation-link.html',
    useShadowDom: false)
class NavigationLinkComponent implements ScopeAware {
  final App app;

  Scope _scope;
  Scope get scope => _scope;
  set scope(Scope value) {
    _scope = value;
    _scope.on('active').listen((event){
      if (event.data == code) {
        active = true;
      }
    });
  }

  @NgAttr('code')
  String code;

  @NgAttr('name')
  String name;

  @NgAttr('url')
  String url;

  @NgAttr('required-role')
  String requiredRole;

  @NgOneWay('has-children')
  bool hasChildren;

  @NgAttr('parent')
  String parent;

  bool _active = false;

  bool get active => _active;
  set active(bool value) {
    if (parent != null && value) {
      scope.parentScope.broadcast('active', parent);
    }
    _active = value;
  }

  NavigationLinkComponent(this.app) {
    app.onBreadCrumbChange.listen((BreadCrumb breadCrumb){
      if (breadCrumb.path.isEmpty) {
        active = code == 'home';
      } else {
        active = breadCrumb.path.any((_) => _.code == code);
      }
    });
  }

  bool get isGranted => requiredRole == null || app.roles.contains(requiredRole);
}