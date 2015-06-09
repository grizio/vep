part of vep.component.main;

@Injectable()
class App {
  final Injector injector;

  App(this.injector);

  /// Username of the current user if connected (session)
  String get username => window.sessionStorage['loginUsername'];

  /// Key of the current user for the current session.
  String get key => window.sessionStorage['loginKey'];

  /// Returns the list of roles of the user.
  List<String> get roles {
    if (isLoggedIn) {
      return window.sessionStorage['loginRoles'].split(',');
    } else {
      return [];
    }
  }

  void login(String username, String key, List<String> roles) {
    window.sessionStorage['loginUsername'] = username;
    window.sessionStorage['loginKey'] = key;
    window.sessionStorage['loginRoles'] = roles.join(',');
    onLogin.process(username);
  }

  void logout() {
    window.sessionStorage.remove('loginUsername');
    window.sessionStorage.remove('loginKey');
    window.sessionStorage.remove('loginRoles');
    onLogout.process(null);
  }

  bool get isLoggedIn => window.sessionStorage.containsKey('loginUsername') && window.sessionStorage.containsKey('loginKey');

  Subscriber onLogin = new Subscriber<String>();

  Subscriber onLogout = new Subscriber();

  Subscriber onBreadCrumbChange = new Subscriber<BreadCrumb>();

  BreadCrumb _breadCrumb;

  BreadCrumb get breadCrumb => _breadCrumb;

  set breadCrumb(BreadCrumb newBreadCrumb) {
    _breadCrumb = newBreadCrumb;
    onBreadCrumbChange.process(newBreadCrumb);
  }
}