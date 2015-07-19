part of vep.component.common.field;

/// This component provides a way for the user to provides a secret text (usually passwords).
@Component(
    selector: 'input-password',
    templateUrl: '/packages/vepweb/component/common/field/input-password.html',
    useShadowDom: false)
class InputPasswordComponent extends InputComponent<String> with InputPatternComponent {
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: 'input.input-password')
class InputPasswordDecorator extends InputDecorator {
  InputPasswordDecorator(Element element) : super(element);
}