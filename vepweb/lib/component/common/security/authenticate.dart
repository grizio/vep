part of vep.component.common.security;

/// This component is used to provide security by hiding a component
/// when the user is not logged or has not the right to see it.
/// When the user is not logged in, show the login form.
@Component(
    selector: 'authenticate',
    templateUrl: '/packages/vepweb/component/common/security/authenticate.html',
    useShadowDom: false)
class Authenticate {
  final App app;

  String _role;

  @NgAttr("role")
  String get role => _role;

  set role(String value) {
    _role = value;
    updateState();
  }

  String _state;

  String get state => _state;

  Authenticate(this.app) {
    updateState();
    app.onLogin.listen((_) => updateState());
    app.onLogout.listen((_) => updateState());
  }

  void updateState() {
    if (app.isLoggedIn) {
      if (app.roles.contains(role)) {
        _state = 'accepted';
      } else {
        _state = 'blocked';
      }
    } else {
      _state = 'login';
    }
  }
}