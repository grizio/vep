part of vep.component.session;

@Component(
    selector: 'form-session',
    templateUrl: '/packages/vepweb/component/session/form-session.html',
    useShadowDom: false)
class FormSessionComponent extends FormStepsComponentContainer {
  static const stateUpdate = 'update';
  static const stateCreate = 'create';

  final Router router;
  final App app;
  final SessionService sessionService;
  final ShowService showService;
  final TheaterService theaterService;

  List<Choice<String>> theaterChoices = [];

  SessionForm session = new SessionForm();

  String current = 'general';

  String state;

  DateTime minDate = new DateTime.now();

  DateTime minReservationEndDate = new DateTime.now();

  DateTime maxReservationEndDate = null;

  InputOneChoiceComponent get theaterField => form['general']['theater'];

  InputTextComponent get nameField => form['summary']['name'];

  InputDateComponent get dateField => form['general']['date'];

  InputDateComponent get reservationEndDateField => form['general']['reservationEndDate'];

  String selectedTheater;

  bool done = false;

  FormSessionComponent(this.router, this.app, this.sessionService, this.showService, this.theaterService, RouteProvider routeProvider);

  Future initialize() {
    theaterField.onValueChange.listen((_) => selectedTheater = _.newValue);
    nameField.enableWhen = () => session.internalShows.length > 1;
    dateField.onValueChange.listen((_) => maxReservationEndDate = _.newValue);
    var futureTheater = theaterService.findAll().then((_){
      theaterChoices = _.map((t) => new Choice(t.canonical, t.name)).toList();
    });
    return Future.wait([futureTheater]);
  }

  Future<HttpResult> postSession(SessionForm data) {
    data.shows = data.internalShows.map((_) => _.show).toList();
    return sessionService.create(data);
  }

  Future onValid(Future<HttpResult> result) {
    done = true;
    current = 'general';
    return new Future.value();
  }

  Future initSummary() {
    if (session.internalShows.length == 1) {
      return showService.find(session.internalShows.first.show).then((_) {
        session.name = _.title;
      });
    } else {
      return new Future.value();
    }
  }
}