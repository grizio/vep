part of vep.component.session;

@Component(
    selector: 'session-card-reservations',
    templateUrl: '/packages/vepweb/component/session/session-card-reservations.html',
    useShadowDom: false)
class SessionCardReservationComponent implements TableSearchContext, AttachAware {
  final SessionService sessionService;
  final TheaterService theaterService;
  final ReservationService reservationService;
  final App app;

  @NgAttr('theater')
  String theater;

  @NgAttr('session')
  String session;

  @override
  TableDescriptor tableDescriptor;

  SessionCardReservationComponent(this.sessionService, this.theaterService, this.reservationService, this.app);

  @override
  void attach() {
    createTableDescriptor();
  }

  void createTableDescriptor() {
    if (app.isLoggedIn && app.roles.contains(roles.sessionManager)) {
      Future.wait([
        theaterService.find(theater),
        sessionService.find(theater, session)
      ]).then((_) {
        final theater = _[0] as Theater;
        final session = _[1] as Session;

        var columns = [
          new ColumnDescriptor('firstName', 'Prénom', ColumnTypes.text, hasFilter: true),
          new ColumnDescriptor('lastName', 'Nom', ColumnTypes.text, hasFilter: true),
          new ColumnDescriptor('email', 'Adresse e-mail', ColumnTypes.text, hasFilter: true),
          new ColumnDescriptor('city', 'Ville', ColumnTypes.text, hasFilter: true),
          new ColumnDescriptor('comment', 'Commentaire', ColumnTypes.text, hasFilter: false)
        ];
        if (theater.fixed) {
          columns.add(new ColumnDescriptor('joinedSeats', 'Places', ColumnTypes.text, hasFilter: false));
        } else {
          columns.add(new ColumnDescriptor('seats', 'Nombre de places', ColumnTypes.text, hasFilter: false));
        }
        for (final price in session.prices) {
          columns.add(new ColumnDescriptor(price.id.toString(), '${price.name} (${price.price / 100}€)', ColumnTypes.text, hasFilter: false));
        }
        columns.add(new ColumnDescriptor('total', 'Total', ColumnTypes.text, hasFilter: false));
        tableDescriptor = new TableDescriptor(columns);
      });
    }
  }

  @override
  Future<List<Map<String, Object>>> search(Map<String, Object> filters) {
    if (app.isLoggedIn && app.roles.contains(roles.sessionManager)) {
      return reservationService.findReservations(theater, session).then((reservations) {
        return reservations.map((Reservation reservation) {
          final result = {
            'firstName': reservation.firstName,
            'lastName': reservation.lastName,
            'email': reservation.email,
            'city': reservation.city,
            'comment': reservation.comment,
            'joinedSeats': reservation.joinedSeats,
            'seats': reservation.seats
          };

          int total = 0;
          for (final price in reservation.prices) {
            total += price.value / 100.0;
            result[price.price.toString()] = price.number;
          }
          result['total'] = total;

          return result;
        }).toList();
      });
    } else {
      return new Future.value([]);
    }
  }

  @override
  void onChange(Map<String, Object> data) {
    // Do nothing
  }
}