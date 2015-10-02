part of vep.component.cms.contact;

@Component(
    selector: 'form-contact',
    templateUrl: '/packages/vepweb/component/cms/contact/form-contact.html',
    useShadowDom: false)
class FormContactComponent extends FormSimpleComponentContainer {
  final ContactService contactService;

  InputEmailComponent get email => form['email'];

  InputEmailComponent get email2 => form['email2'];

  bool done = false;

  Contact contact = new Contact();

  FormContactComponent(this.contactService);

  void initialize() {
    email.onValueChange.listen((_) => email2.verify());
    email2.addConstraint((v) => stringUtilities.isEmpty(v) || stringUtilities.equals(email.value, v), errorCodes.i18n[errorCodes.differentEmail]);
  }

  Future<HttpResult> onSubmit(Contact data) {
    return contactService.post(data);
  }

  Future onDone(HttpResult httpResult) {
    done = true;
    return new Future.value();
  }
}