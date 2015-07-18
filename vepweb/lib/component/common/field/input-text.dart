part of vep.component.common.field;

@Component(
    selector: 'input-text',
    templateUrl: '/packages/vepweb/component/common/field/input-text.html',
    useShadowDom: false)
class InputTextComponent extends InputComponent<String> with InputPatternComponent {
  @NgOneWayOneTime('area')
  bool area = false;
}

@Decorator(selector: '.input-text')
class InputTextDecorator extends InputDecorator {
    InputTextDecorator(Element element) : super(element);
}