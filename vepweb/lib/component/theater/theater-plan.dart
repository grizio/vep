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

  /// "all", "none", "description"
  @NgOneWay('legend')
  String legend = "all";

  @NgOneWay('json-plan')
  String get jsonPlan => _jsonPlan;

  set jsonPlan(String value) {
    _jsonPlan = value;
    try {
      seats = jsonx.decode(value, type: const jsonx.TypeHelper<List<Seat>>().type);
      updateTaken();
      if (decorator != null) {
        decorator.update();
      }
    } catch (e) {
      seats = null;
    }
  }

  List<String> _selected = [];

  @NgTwoWay('selected')
  List<String> get selected => _selected;

  set selected(List<String> selected) => _selected = selected != null ? selected : [];

  List<String> _taken = [];

  @NgOneWay('taken')
  List<String> get taken => _taken;

  set taken(List<String> taken) {
    _taken = taken != null ? taken : [];
    updateTaken();
  }

  void updateTaken() {
    if (seats != null) {
      seats.forEach((_) => _.taken = taken.contains(_.c));
    }
  }

  void toggle(Seat seat) {
    if (enabled && seat.t != 'taken' && seat.t != 'stage') {
      if (seat.selected) {
        seat.selected = false;
        if (selected != null) {
          selected.remove(seat.c);
        }
      } else {
        seat.selected = true;
        if (selected != null && !selected.contains(seat.c)) {
          selected.add(seat.c);
        }
      }
    }
  }
}

@Decorator(selector: '.theater-plan-map')
class TheaterPlanMapDecorator implements AttachAware {
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
    // ?: because of some bug sometimes
    var width = element.parent.clientWidth != 0 ? min(element.parent.clientWidth, maxWidth) : maxWidth;
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

  @override
  void attach() {
    update();
  }
}