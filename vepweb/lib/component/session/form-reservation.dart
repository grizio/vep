part of vep.component.session;

@Component(
    selector: 'form-reservation',
    templateUrl: '/packages/vepweb/component/session/form-reservation.html',
    useShadowDom: false)
class FormReservationComponent extends FormStepsComponentContainer {
  final TheaterService theaterService;
  final SessionService sessionService;
  final ReservationService reservationService;
  final App app;
  final Router router;
  final RouteProvider routeProvider;

  static const stepSelectNumber = 'select-number';
  static const stepSelectPlan = 'select-plan';

  String current;

  ReservationForm reservation = new ReservationForm();

  Theater theater;

  Session session;

  List<Choice<int>> prices;

  int reservedSeats = 0;

  /// For fixed theater
  List<String> takenSeats;

  /// For dynamic theater
  int takenSeatsCount;

  FormStepComponent get selectPlan => form['select-plan'];

  InputIntegerComponent get seats => form['select-number']['seats'];

  InputEmailComponent get email => form['person']['email'];

  InputEmailComponent get email2 => form['person']['email2'];

  ReservationResult result = null;

  FormReservationComponent(this.theaterService, this.sessionService, this.reservationService, this.app, this.router, this.routeProvider);

  Future onLoaded() {
    final futureTheater = theaterService.find(routeProvider.parameters['theater']).then((_) {
      theater = _;
      current = theater.fixed ? stepSelectPlan : stepSelectNumber;
    });

    final futureSession = sessionService.find(routeProvider.parameters['theater'], routeProvider.parameters['session']).then((_) {
      session = _;
      prices = session.prices.map((_) {
        if (stringUtils.isNotBlank(_.condition)) {
          return new Choice(_.id, '${_.name} (${_.price / 100}€ - ${_.condition})');
        } else {
          return new Choice(_.id, '${_.name} (${_.price / 100}€)');
        }
      }).toList();
    });

    final futureReservedSeats = futureTheater.then((_) {
      if (theater.fixed) {
        reservationService.findReservedPlacesPlan(routeProvider.parameters['theater'], routeProvider.parameters['session']).then((_) {
          takenSeats = _;
        });
      } else {
        reservationService.findReservedPlacesNumber(routeProvider.parameters['theater'], routeProvider.parameters['session']).then((_) {
          takenSeatsCount = _;
        });
      }
    });

    seats.onValueChange.listen((event) => reservedSeats = event.newValue);

    email.onValueChange.listen((_) => email2.verify());

    email2.addConstraint((v) => stringUtils.isEmpty(v) || stringUtils.equals(email.value, v), errorCodes.i18n[errorCodes.differentEmail]);

    return Future.wait([
      futureTheater,
      futureSession,
      futureReservedSeats
    ]);
  }

  Future<bool> validateSelectPlan() {
    selectPlan.errors.clear();
    if (reservation.seatList.isEmpty) {
      selectPlan.errors.add('Veuillez sélectionner au moins une place.');
      return new Future.value(false);
    } else {
      return new Future.value(true);
    }
  }

  Future leavingSelectPlanToNextStep() {
    reservedSeats = reservation.seatList.length;
    return new Future.value();
  }

  Future leavingPricesToNextStep() {
    final prices = <ReservationPrice>[];
    reservation.priceRepartition.forEach((price, count) {
      final ref = session.prices.firstWhere((_) => _.id == price, orElse: () => null);
      final rp = new ReservationPrice();
      rp.price = price;
      rp.number = count;
      rp.name = ref != null ? ref.name : '';
      rp.value = ref != null ? ref.price : 0;
      prices.add(rp);
    });
    reservation.prices = prices;
    return new Future.value();
  }

  Future<HttpResult> onSubmit(ReservationForm data) {
    return reservationService.create(session.theater, session.canonical, data);
  }

  Future onValid(Future<HttpResult> httpResult) {
    final map = (httpResult as HttpResultSuccessEntity).entity as Map;
    result = new ReservationResult();
    result.id = map['id'];
    result.key = map['key'];
    return new Future.value();
  }
}