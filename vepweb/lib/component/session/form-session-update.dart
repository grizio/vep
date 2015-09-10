part of vep.component.session;

@Component(
    selector: 'form-session-update',
    templateUrl: '/packages/vepweb/component/session/form-session-update.html',
    useShadowDom: false)
class FormSessionUpdateComponent extends FormStepsComponentContainer {
  final Router router;
  final App app;
  final SessionService sessionService;
  final ShowService showService;
  final TheaterService theaterService;

  List<Choice<String>> theaterChoices = [];

  SessionFormUpdate session = new SessionFormUpdate();

  String current = 'general';

  DateTime minDate = new DateTime.now();

  DateTime minReservationEndDate = new DateTime.now();

  DateTime maxReservationEndDate = null;

  InputOneChoiceComponent get theaterField => form['general']['theater'];

  InputTextComponent get nameField => form['summary']['name'];

  InputDateComponent get dateField => form['general']['date'];

  InputDateComponent get reservationEndDateField => form['general']['reservationEndDate'];

  String selectedTheater;

  FormSessionUpdateComponent(this.router, this.app, this.sessionService, this.showService, this.theaterService, RouteProvider routeProvider) {
    sessionService.find(routeProvider.parameters['theater'], routeProvider.parameters['canonical']).then((_){
      session = _.toSessionFormUpdate();
    });
  }

  Future initialize() {
    theaterField.onValueChange.listen((_) => selectedTheater = _.newValue);
    nameField.enableWhen = () => session.internalShows != null && session.internalShows.length > 1;
    dateField.onValueChange.listen((_) => maxReservationEndDate = _.newValue);
    var futureTheater = theaterService.findAll().then((_){
      theaterChoices = _.map((t) => new Choice(t.canonical, t.name)).toList();
    });
    return Future.wait([futureTheater]);
  }

  Future<HttpResult> postSession(SessionForm data) {
    data.shows = data.internalShows.map((_) => _.show).toList();
    return sessionService.update(data);
  }

  Future onValid(Future<HttpResult> result) {
    router.gotoUrl('/sessions');
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