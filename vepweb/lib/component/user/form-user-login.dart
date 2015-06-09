part of vep.component.user;

@Component(
    selector: 'form-user-login',
    templateUrl: '/packages/vepweb/component/user/form-user-login.html',
    useShadowDom: false)
class FormUserLoginComponent extends FormComponent<UserLogin> {
  final UserService userService;
  final Router router;
  final App app;

  @NgOneWay('redirect-back')
  bool redirectBack = true;

  FormUserLoginComponent(this.userService, this.router, this.app);

  @override
  UserLogin get formData => user;

  UserLogin user = new UserLogin();

  @override
  Future<HttpResult> onSubmit() => userService.login(user);

  @override
  void onDone(HttpResult httpResult) {
    var res = httpResult as HttpResultSuccess;
    userService.getRoles(user.email, res.body).then((_){
      if (_.isSuccess) {
        app.login(user.email, res.body, (_ as HttpResultSuccessEntity).entity);
      } // else Should never happen

      // We return on the previous page
      if (redirectBack) {
        window.history.back();
      }
    });
  }
}