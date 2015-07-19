part of vep.component.common.field;

/// This component provides a way for the user to provides a simple text.
@Component(
    selector: 'input-text',
    templateUrl: '/packages/vepweb/component/common/field/input-text.html',
    useShadowDom: false)
class InputTextComponent extends InputComponent<String> with InputPatternComponent {
  @NgOneWayOneTime('area')
  bool area = false;
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: '.input-text')
class InputTextDecorator extends InputDecorator {
  InputTextDecorator(Element element) : super(element);
}