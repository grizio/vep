part of vep.component.common.field;

@Component(
    selector: 'input-password',
    templateUrl: '/packages/vepweb/component/common/field/input-password.html',
    useShadowDom: false)
class InputPasswordComponent extends InputComponent<String> with InputPatternComponent {
}

@Decorator(selector: 'input.input-password')
class InputPasswordDecorator extends InputDecorator {
  InputPasswordDecorator(Element element) : super(element);
}