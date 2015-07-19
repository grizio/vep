part of vep.component.common.misc;

abstract class MapContainer {
  set mapComponent(MapComponent map);
}

typedef void OnMapInitialized();

class MapInitializedSubscriber {
  List<OnMapInitialized> _listeners = [];

  void process() {
    _listeners.forEach((listener) {
      listener();
    });
  }

  void listen(OnMapInitialized listener) {
    _listeners.add(listener);
  }
}

@Component(
    selector: 'map',
    templateUrl: '/packages/vepweb/component/common/misc/map.html',
    useShadowDom: false)
class MapComponent implements ScopeAware {
  static const defaultLat = 47.00;
  static const defaultLng = 03.00;
  static const defaultInnerClass = 'map-250';
  static const defaultZoom = 8;

  double _lat = defaultLat;
  double _lng = defaultLng;
  String _innerClass = defaultInnerClass;
  int _zoom = defaultZoom;

  @NgOneWayOneTime('mark-center')
  bool markCenter = false;

  @NgAttr('innerClass')
  String get innerClass => _innerClass;

  set innerClass(String value) {
    if (value != null) {
      _innerClass = value;
    } else {
      _innerClass = defaultInnerClass;
    }
  }

  @NgTwoWay('lat')
  double get lat => _lat;

  set lat(double lat) {
    _lat = lat != null ? lat : defaultLat;
    mapDecorator.forEach((_) => _.update());
  }

  @NgTwoWay('lng')
  double get lng => _lng;

  set lng(double lng) {
    _lng = lng != null ? lng : defaultLng;
    mapDecorator.forEach((_) => _.update());
  }

  @NgTwoWay('zoom')
  int get zoom => _zoom;

  set zoom(int zoom) {
    _zoom = zoom != null ? zoom : defaultZoom;
    mapDecorator.forEach((_) => _.update());
  }

  Option<MapDecorator> mapDecorator = None;

  @override
  set scope(Scope scope) {
    var ctx = utils.getContext(scope, MapContainer) as MapContainer;
    if (ctx != null) {
      ctx.mapComponent = this;
    }
  }

  final MapInitializedSubscriber mapInitializedSubscriber = new MapInitializedSubscriber();

  Option<GMap> get map => mapDecorator.map((_) => _.gmap);

  int addMarker(LatLng latLng, String title) => mapDecorator.map((_) => _.addMarker(latLng, title)).getOrElse(() => 0);

  void removeMarker(int id) => mapDecorator.forEach((_) => _.removeMarker(id));
}

@Decorator(selector: '.map')
class MapDecorator implements ScopeAware, AttachAware {
  static int _currentId = 0;

  final Element element;
  Scope _scope;
  MapComponent mapComponent;
  GMap gmap;

  Map<int, Marker> markers = {};

  Scope get scope => _scope;

  @override
  set scope(Scope scope) {
    _scope = scope;
    mapComponent = utils.getContext(scope, MapComponent) as MapComponent;
    if (mapComponent != null) {
      mapComponent.mapDecorator = Some(this);
    }
  }

  MapDecorator(this.element);

  @override
  void attach() {
    update();
  }

  void update() {
    if (mapComponent != null) {
      var options = new MapOptions();
      var center = new LatLng(mapComponent.lat, mapComponent.lng);
      options.zoom = mapComponent.zoom;
      options.center = center;
      options.mapTypeId = MapTypeId.ROADMAP;
      gmap = new GMap(element, options);
      if (mapComponent.markCenter) {
        addMarker(center, null);
      }
      mapComponent.mapInitializedSubscriber.process();
    }
  }

  int addMarker(LatLng latLng, String title) {
    if (gmap != null) {
      var options = new MarkerOptions();
      options.position = latLng;
      options.map = gmap;
      options.title = title;
      var marker = new Marker(options);
      _currentId++;
      markers[_currentId] = marker;
      return _currentId;
    } else {
      return 0;
    }
  }

  void removeMarker(int id) {
    if (markers.containsKey(id)) {
      markers[id].map = null;
      markers.remove(id);
    }
  }
}