part of vep.component.common.security;

/// This component is used to provide security by hiding a component
/// when the user is not logged or has not the right to see it.
@Component(
    selector: 'on-granted',
    templateUrl: '/packages/vepweb/component/common/security/on-granted.html',
    useShadowDom: false)
class OnGranted {
  final App app;

  String _role;

  @NgAttr("role")
  String get role => _role;

  set role(String value) {
    _role = value;
    updateState();
  }

  bool accepted = false;

  OnGranted(this.app) {
    updateState();
    app.onLogin.listen((_) => updateState());
    app.onLogout.listen((_) => updateState());
  }

  void updateState() {
    accepted = app.isLoggedIn && app.roles.contains(role);
  }
}