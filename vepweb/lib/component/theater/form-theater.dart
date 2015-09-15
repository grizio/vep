part of vep.component.theater;

@Component(
    selector: 'form-theater',
    templateUrl: '/packages/vepweb/component/theater/form-theater.html',
    useShadowDom: false)
class FormTheaterComponent extends FormStepsComponentContainer {
  static const stateUpdate = 'update';
  static const stateCreate = 'create';

  final TheaterService theaterService;
  final Router router;
  final App app;


  final List<Choice<bool>> theaterTypes = [new Choice(true, 'Fixe'), new Choice(false, 'Libre')];

  TheaterForm theater = new TheaterForm();

  String current = 'address';

  String state;

  String currentCanonical;

  InputTextComponent get name => form['address']['name'];

  InputTextComponent get canonical => form['address']['canonical'];

  String _oldCanonical;

  FormTheaterComponent(this.theaterService, this.router, this.app, RouteProvider routeProvider) {
    if (routeProvider.route.name == 'theater-update') {
      state = stateUpdate;
      currentCanonical = routeProvider.parameters['canonical'];
    } else {
      state = stateCreate;
    }
  }

  Future initialize() {
    name.onValueChange.listen((_) {
      if (stringUtilities.isEmpty(_oldCanonical) && stringUtilities.isEmpty(canonical.value) || stringUtilities.equals(_oldCanonical, canonical.value)) {
        canonical.value = utils.canonicalize(name.value);
      }
      _oldCanonical = utils.canonicalize(name.value);
    });
    canonical.enableWhen = () => state == stateCreate;

    if (state == stateUpdate) {
      return _load();
    } else {
      return new Future.value();
    }
  }

  Future _load() {
    return theaterService.find(currentCanonical).then((_) {
      theater = _.toTheaterForm();
      app.breadCrumb.path.last.name = 'Mise à jour du théâtre ${theater.name}';
    });
  }

  Future<HttpResult> postTheater(Object data) {
    if (state == stateCreate) {
      return theaterService.create(data as TheaterForm);
    } else {
      return theaterService.update(data as TheaterForm);
    }
  }

  Future onValid(Future<HttpResult> result) {
    router.gotoUrl('/theaters');
    return new Future.value();
  }
}