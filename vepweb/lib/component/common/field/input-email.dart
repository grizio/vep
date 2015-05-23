part of vep.component.common.field;

@Component(
    selector: 'input-email',
    templateUrl: '/packages/vepweb/component/common/field/input-email.html',
    useShadowDom: false)
class InputEmailComponent extends InputComponent<String> {
}

@Decorator(selector: 'input.input-email')
class InputEmailDecorator extends InputDecorator {
  InputEmailDecorator(Element element) : super(element);
}