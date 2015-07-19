part of vep.component.theater;

@Component(
    selector: 'theater-card',
    templateUrl: '/packages/vepweb/component/theater/theater-card.html',
    useShadowDom: false
)
class TheaterCardComponent implements AttachAware {
  final TheaterService theaterService;
  final RouteProvider routeProvider;
  final App app;
  final GeocodingService geocodingService;

  @NgOneWayOneTime('canonical')
  String canonical;

  @NgOneWayOneTime('small')
  bool small = false;

  Theater theater;

  int theaterLat;
  int theaterLng;

  TheaterCardComponent(this.theaterService, this.routeProvider, this.app, this.geocodingService);

  @override
  void attach() {
    theaterService.find(getCanonical()).then((_) {
      theater = _;
      if (app.breadCrumb.path.last.code == 'theater-read') {
        app.breadCrumb.path.last.name = theater.name;
      }
      initializeMapPosition();
    });
  }

  String getCanonical() {
    if (canonical != null) {
      return canonical;
    } else if (routeProvider.parameters.containsKey('canonical')) {
      return routeProvider.parameters['canonical'];
    } else {
      return null;
    }
  }

  void initializeMapPosition() {
    geocodingService.findLatLngByAddress(theater.address).then((_) {
      theaterLat = _.lat;
      theaterLng = _.lng;
    });
  }
}