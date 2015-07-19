part of vep.component.user;

@Component(
    selector: 'form-user-login',
    templateUrl: '/packages/vepweb/component/user/form-user-login.html',
    useShadowDom: false)
class FormUserLoginComponent extends FormSimpleComponentContainer {
  final UserService userService;
  final Router router;
  final App app;

  final List<String> fields = [];

  @NgOneWay('redirect-back')
  bool redirectBack = true;

  UserLogin userLogin = new UserLogin();

  FormUserLoginComponent(this.userService, this.router, this.app);

  Future<HttpResult> onSubmit(UserLogin data) => userService.login(data);

  Future onDone(HttpResult httpResult) {
    var res = httpResult as HttpResultSuccess;
    userService.getRoles(userLogin.email, res.body).then((_) {
      if (_.isSuccess) {
        app.login(userLogin.email, res.body, (_ as HttpResultSuccessEntity).entity);
      }
      // else Should never happen

      // We return on the previous page
      if (redirectBack) {
        window.history.back();
      }
    });
    return new Future.value();
  }
}