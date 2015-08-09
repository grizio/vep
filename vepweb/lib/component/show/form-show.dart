part of vep.component.show;

@Component(
    selector: 'form-show',
    templateUrl: '/packages/vepweb/component/show/form-show.html',
    useShadowDom: false)
class FormShowComponent extends FormSimpleComponentContainer {
  static const stateUpdate = 'update';
  static const stateCreate = 'create';

  List<Choice<String>> companyChoices = [];

  final CompanyService companyService;
  final ShowService showService;
  final Router router;
  final App app;
  final NavigationComponent navigationComponent;

  InputTextComponent get title => form['title'];

  InputTextComponent get canonical => form['canonical'];

  bool loaded = true;

  String state;

  ShowForm show = new ShowForm();

  String get formTitle {
    if (state == stateCreate) {
      return "Création d'une pièce";
    } else if (show.title != null) {
      return "Modification de la pièce " + show.title;
    } else {
      return "Modification de la pièce";
    }
  }

  FormShowComponent(this.companyService, this.showService, this.router, this.app, this.navigationComponent, RouteProvider routeProvider) {
    if (routeProvider.route.name == 'show-update') {
      state = stateUpdate;
      _load(routeProvider);
    } else {
      state = stateCreate;
    }
    companyService.findAll().then((l) => companyChoices = l.map((c) => new Choice(c.canonical, c.name)).toList());
  }

  String _oldCanonical;

  void initialize() {
    title.onValueChange.listen((_) {
      if (stringUtilities.isEmpty(_oldCanonical) && stringUtilities.isEmpty(canonical.value) || stringUtilities.equals(_oldCanonical, canonical.value)) {
        canonical.value = utils.canonicalize(title.value);
      }
      _oldCanonical = utils.canonicalize(title.value);
    });
    canonical.enableWhen = () => state == stateCreate;
  }

  void _load(RouteProvider routeProvider) {
    loaded = false;
    showService.find(routeProvider.parameters['canonical']).then((_) {
      show = _.toShowForm();
      app.breadCrumb.path.last.name = formTitle;
      loaded = true;
    });
  }

  Future<HttpResult> onSubmit(ShowForm data) {
    if (state == stateCreate) {
      return showService.create(data);
    } else {
      return showService.update(data);
    }
  }

  Future onDone(HttpResult httpResult) {
    router.gotoUrl('/shows');
    return new Future.value();
  }
}