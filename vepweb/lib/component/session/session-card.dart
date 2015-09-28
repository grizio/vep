part of vep.component.session;

@Component(
    selector: 'session-card',
    templateUrl: '/packages/vepweb/component/session/session-card.html',
    useShadowDom: false
)
class SessionCardComponent implements AttachAware {
  final ShowService showService;
  final TheaterService theaterService;
  final SessionService sessionService;
  final ReservationService reservationService;
  final RouteProvider routeProvider;
  final App app;

  @NgOneWayOneTime('theater')
  String theater;

  @NgOneWayOneTime('canonical')
  String canonical;

  @NgOneWayOneTime('small')
  bool small = false;

  SessionCard sessionCard;

  List<String> takenSeats;

  int takenSeatNumber;

  SessionCardComponent(this.showService, this.theaterService, this.sessionService, this.reservationService, this.routeProvider, this.app);

  @override
  void attach() {
    sessionService.find(getTheaterCanonical(), getCanonical()).then((session) {
      final loadingSessionCard = new SessionCard();
      copyByAccessors(session, loadingSessionCard);
      loadingSessionCard.shows = new List.generate(session.shows.length, (i) => null);
      var futures = <Future>[];

      // load theater
      final theaterFuture = theaterService.find(session.theater).then((theater) {
        loadingSessionCard.theater = theater;
      });
      futures.add(theaterFuture);

      futures.add(theaterFuture.then((_){
        if (loadingSessionCard.theater.fixed) {
          return reservationService.findReservedPlacesPlan(getTheaterCanonical(), getCanonical()).then((_){
            takenSeats = _;
          });
        } else {
          return reservationService.findReservedPlacesNumber(getTheaterCanonical(), getCanonical()).then((_){
            takenSeatNumber = _;
          });
        }
      }));

      // load shows
      futures.addAll(new List.generate(session.shows.length, (i) => showService.find(session.shows[i]).then((_) {
        loadingSessionCard.shows[i] = _;
      })));

      // waiting before setting in model
      Future.wait(futures).then((_) {
        sessionCard = loadingSessionCard;
      });

      // Updating breadcrumb
      if (app.breadCrumb.path.last.code == 'session-read') {
        app.breadCrumb.path.last.name = session.name;
      }
    });
  }

  String getTheaterCanonical() {
    if (theater != null) {
      return theater;
    } else if (routeProvider.parameters.containsKey('theater')) {
      return routeProvider.parameters['theater'];
    } else {
      return null;
    }
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
}