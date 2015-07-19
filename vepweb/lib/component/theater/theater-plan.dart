part of vep.component.theater;

@Component(
    selector: 'theater-plan',
    templateUrl: '/packages/vepweb/component/theater/theater-plan.html',
    useShadowDom: false)
class TheaterPlanComponent {
  String _jsonPlan;
  List<Seat> seats;
  TheaterPlanMapDecorator decorator;

  @NgOneWay('enabled')
  bool enabled;

  @NgOneWay('json-plan')
  String get jsonPlan => _jsonPlan;

  set jsonPlan(String value) {
    _jsonPlan = value;
    try {
      seats = jsonx.decode(value, type: const jsonx.TypeHelper<List<Seat>>().type);
      decorator.update();
    } catch (e) {
      seats = null;
    }
  }

  List<String> get selected => seats.where((_) => _.selected).map((_) => _.c);

  void toggle(Seat seat) {
    if (seat.t != 'taken') {
      seat.selected = !seat.selected;
    }
    // else nothing, we cannot select a taken seat
  }
}

@Decorator(selector: '.theater-plan-map')
class TheaterPlanMapDecorator {
  TheaterPlanComponent component;
  Element element;

  TheaterPlanMapDecorator(this.element, this.component) {
    component.decorator = this;
    update();
  }

  void update() {
    var maxWidth = 0, maxHeight = 0;
    if (component.seats != null) {
      for (var seat in component.seats) {
        maxWidth = max(maxWidth, seat.x + seat.w);
        maxHeight = max(maxHeight, seat.y + seat.h);
      }
    }
    maxWidth += 10;
    maxHeight += 10;
    var width = min(element.parent.clientWidth, maxWidth);
    var height = min(window.innerHeight - 25, maxHeight);
    element.style.width = width.toString() + 'px';
    element.style.height = height.toString() + 'px';

    // Correct behavior when scroll appeared once
    if (maxWidth == width && maxHeight == height) {
      element.style.overflow = 'hidden';
    } else {
      element.style.overflow = 'auto';
    }
  }
}