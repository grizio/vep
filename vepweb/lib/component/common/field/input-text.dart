part of vep.component.common.field;

@Component(
    selector: 'input-text',
    templateUrl: '/packages/vepweb/component/common/field/input-text.html',
    useShadowDom: false)
class InputTextComponent extends InputComponent<String> with InputPatternComponent {
}

@Decorator(selector: 'input.input-text')
class InputTextDecorator extends InputDecorator {
    InputTextDecorator(Element element) : super(element);
}