part of vep.component.common.field;

/// This component provides a way for the user to enter an email address and check its format (from browser validator).
@Component(
    selector: 'input-email',
    templateUrl: '/packages/vepweb/component/common/field/input-email.html',
    useShadowDom: false)
class InputEmailComponent extends InputComponent<String> {
}

/// Decorates the input to add attributes in terms of developer configuration.
@Decorator(selector: 'input.input-email')
class InputEmailDecorator extends InputDecorator {
  InputEmailDecorator(Element element) : super(element);
}