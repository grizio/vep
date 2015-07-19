part of vep.component.theater;

@Component(
    selector: 'input-theater',
    templateUrl: '/packages/vepweb/component/theater/input-theater.html',
    useShadowDom: false
)
class InputTheaterComponent extends InputTextComponent {
}

@Decorator(selector: '.input-theater')
class InputTheaterDecorator extends InputDecorator {
  InputTheaterDecorator(Element element) : super(element);
}