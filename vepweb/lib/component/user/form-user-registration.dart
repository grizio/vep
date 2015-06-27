part of vep.component.user;

@Component(
    selector: 'form-user-registration',
    templateUrl: '/packages/vepweb/component/user/form-user-registration.html',
    useShadowDom: false)
class FormUserRegistrationComponent extends FormComponent<UserRegistration> {
  final UserService userService;

  final List<String> fields = ['email', 'email2', 'password', 'password2'];

  InputEmailComponent get email => getField('email');
  InputEmailComponent get email2 => getField('email2');
  InputPasswordComponent get password => getField('password');
  InputPasswordComponent get password2 => getField('password2');

  FormUserRegistrationComponent(this.userService) {
    formData = new UserRegistration();
  }

  @override
  void initialize() {
    email.onValueChange.listen((_) => email2.verify());

    // String.isEmpty to avoid showing error because it is not already set.
    email2.addConstraint((v) => stringUtilities.isEmpty(v) || stringUtilities.equals(email.value, v), errorCodes.i18n[errorCodes.differentEmails]);

    password.addConstraint((v) => stringUtilities.isSecuredPassword(v, needLowercaseLetter: false, needUppercaseLetter: false, needSymbol: false, minLength: 8), errorCodes.i18n[errorCodes.weakPassword]);
    password.onValueChange.listen((_) => password2.verify());

    // String.isEmpty to avoid showing error because it is not already set.
    password2.addConstraint((v) => stringUtilities.isEmpty(v) || stringUtilities.equals(password.value, v), errorCodes.i18n[errorCodes.differentPasswords]);
  }

  @override
  Future<HttpResult> onSubmit(UserRegistration data) => userService.register(data);
}