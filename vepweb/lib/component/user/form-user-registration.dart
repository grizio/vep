part of vep.component.user;

@Component(
    selector: 'form-user-registration',
    templateUrl: '/packages/vepweb/component/user/form-user-registration.html',
    useShadowDom: false)
class FormUserRegistrationComponent extends FormComponent<UserRegistration> {
  final UserService userService;

  FormUserRegistrationComponent(this.userService);

  InputEmailComponent _email;

  InputEmailComponent get email => _email;

  set email(InputEmailComponent email) {
    _email = email;
    _email.onValueChange.listen((_) => email2.verify());
  }

  InputEmailComponent _email2;

  InputEmailComponent get email2 => _email2;

  set email2(InputEmailComponent email2) {
    _email2 = email2;
    // String.isEmpty to avoid showing error because it is not already set.
    _email2.addConstraint((v) => stringUtilities.isEmpty(v) || stringUtilities.equals(email.value, v), errorCodes.i18n[errorCodes.differentEmails]);
  }

  InputPasswordComponent _password;

  InputPasswordComponent get password => _password;

  set password(InputPasswordComponent _) {
    _password = _;
    _password.addConstraint((v) => stringUtilities.isSecuredPassword(v, needLowercaseLetter: false, needUppercaseLetter: false, needSymbol: false, minLength: 8), errorCodes.i18n[errorCodes.weakPassword]);
    _password.onValueChange.listen((_) => _password2.verify());
  }

  InputPasswordComponent _password2;

  InputPasswordComponent get password2 => _password2;

  set password2(InputPasswordComponent _) {
    _password2 = _;
    // String.isEmpty to avoid showing error because it is not already set.
    _password2.addConstraint((v) => stringUtilities.isEmpty(v) || stringUtilities.equals(password.value, v), errorCodes.i18n[errorCodes.differentPasswords]);
  }

  @override
  UserRegistration get formData => user;

  UserRegistration user = new UserRegistration();

  @override
  Future<HttpResult> onSubmit() => userService.register(user);
}