part of vep.component.user;

@Component(
    selector: 'form-user-login',
    templateUrl: '/packages/vepweb/component/user/form-user-login.html',
    useShadowDom: false)
class FormUserLoginComponent extends FormComponent<UserLogin> {
  final UserService userService;
  final Router router;
  final App app;

  FormUserLoginComponent(this.userService, this.router, this.app);

  @override
  UserLogin get formData => user;

  UserLogin user = new UserLogin();

  @override
  Future<HttpResult> onSubmit() => userService.login(user);

  @override
  void onDone(HttpResult httpResult) {
    var res = httpResult as HttpResultSuccess;
    app.login(user.email, res.body);

    // We return on the previous page
    window.history.back();
  }
}