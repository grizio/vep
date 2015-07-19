part of vep.component.common.field;

@Component(
    selector: 'input-address',
    templateUrl: '/packages/vepweb/component/common/field/input-address.html',
    useShadowDom: false)
class InputAddressComponent extends InputTextComponent implements MapContainer {
  MapComponent _mapComponent;
  InputAddressDecorator inputAddressDecorator;

  MapComponent get mapComponent => _mapComponent;

  @override
  set mapComponent(MapComponent mapComponent) {
    _mapComponent = mapComponent;
    _mapComponent.mapInitializedSubscriber.listen((){
      if (inputAddressDecorator != null) {
        inputAddressDecorator.init();
      }
    });
  }

  @override
  set value(String value) {
    super.value = value;
  }
}

@Decorator(selector: '.input-address')
class InputAddressDecorator extends InputDecorator {
  InputAddressDecorator(Element element) : super(element);

  InputAddressComponent ctx;
  int markerId;

  @override
  set scope(Scope scope) {
    super.scope = scope;
    ctx = utils.getContext(scope, InputAddressComponent) as InputAddressComponent;
    ctx.inputAddressDecorator = this;
    init();
  }

  void init() {
    if (ctx != null && ctx.mapComponent != null) {
      ctx.mapComponent.map.forEach((map) {
        final autocomplete = new Autocomplete(element);
        autocomplete.bindTo('bounds', map);

        autocomplete.onPlaceChanged.listen((_) {
          if (markerId != null) {
            ctx.mapComponent.removeMarker(markerId);
          }
          final place = autocomplete.place;
          if (place != null && place.geometry != null) {
            if (place.geometry.viewport != null) {
              map.fitBounds(place.geometry.viewport);
            } else {
              map.center = place.geometry.location;
              map.zoom = 17;
            }
            markerId = ctx.mapComponent.addMarker(
                place.geometry.location,
                place.addressComponents.map((_) => (_ != null && _.shortName != null) ? _.shortName : '').join(' ')
            );
          }
          ctx.value = (element as InputElement).value;
        });
      });
    }
  }
}