part of vep.component.user;

@Component(
    selector: 'form-user-registration',
    templateUrl: '/packages/vepweb/component/user/form-user-registration.html',
    useShadowDom: false)
class FormUserRegistrationComponent extends FormSimpleComponentContainer {
  final UserService userService;

  UserRegistration user = new UserRegistration();

  InputEmailComponent get email => form['email'];
  InputEmailComponent get email2 => form['email2'];
  InputPasswordComponent get password => form['password'];
  InputPasswordComponent get password2 => form['password2'];

  FormUserRegistrationComponent(this.userService);

  void initialize() {
    email.onValueChange.listen((_) => email2.verify());

    // String.isEmpty to avoid showing error because it is not already set.
    email2.addConstraint((v) => stringUtilities.isEmpty(v) || stringUtilities.equals(email.value, v), errorCodes.i18n[errorCodes.differentEmails]);

    password.addConstraint((v) => stringUtilities.isSecuredPassword(v, needLowercaseLetter: false, needUppercaseLetter: false, needSymbol: false, minLength: 8), errorCodes.i18n[errorCodes.weakPassword]);
    password.onValueChange.listen((_) => password2.verify());

    // String.isEmpty to avoid showing error because it is not already set.
    password2.addConstraint((v) => stringUtilities.isEmpty(v) || stringUtilities.equals(password.value, v), errorCodes.i18n[errorCodes.differentPasswords]);
  }

  Future<HttpResult> onSubmit(UserRegistration data) {
    return userService.register(data);
  }
}