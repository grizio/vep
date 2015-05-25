part of vep.component.main;

@Injectable()
class App {
  /// Username of the current user if connected (session)
  String get username => window.sessionStorage['loginUsername'];

  /// Key of the current user for the current session.
  String get key => window.sessionStorage['loginKey'];

  void login(String username, String key) {
    window.sessionStorage['loginUsername'] = username;
    window.sessionStorage['loginKey'] = key;
    onLogin.process(username);
  }

  void logout() {
    window.sessionStorage.remove('loginUsername');
    window.sessionStorage.remove('loginKey');
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