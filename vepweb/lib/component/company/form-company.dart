part of vep.component.company;

@Component(
    selector: 'form-company',
    templateUrl: '/packages/vepweb/component/company/form-company.html',
    useShadowDom: false)
class FormCompanyComponent extends FormSimpleComponentContainer {
  static const stateUpdate = 'update';
  static const stateCreate = 'create';

  final List<Choice<bool>> isVepTypes = [new Choice(true, 'Oui'), new Choice(false, 'Non')];

  final CompanyService companyService;
  final Router router;
  final App app;
  final NavigationComponent navigationComponent;

  String get formTitle {
    if (state == stateCreate) {
      return "Création d'une troupe";
    } else if (company.name != null) {
      return "Modification de la troupe " + company.name;
    } else {
      return "Modification de la troupe";
    }
  }

  InputTextComponent get name => form['name'];

  InputTextComponent get canonical => form['canonical'];

  bool loaded = true;
  String state;

  CompanyForm company = new CompanyForm();

  FormCompanyComponent(this.companyService, this.router, this.app, this.navigationComponent, RouteProvider routeProvider) {
    if (routeProvider.route.name == 'company-update') {
      state = stateUpdate;
      _load(routeProvider);
    } else {
      state = stateCreate;
    }
  }

  String _oldCanonical;

  void initialize() {
    name.onValueChange.listen((_) {
      if (stringUtilities.isEmpty(_oldCanonical) && stringUtilities.isEmpty(canonical.value) || stringUtilities.equals(_oldCanonical, canonical.value)) {
        canonical.value = utils.canonicalize(name.value);
      }
      _oldCanonical = utils.canonicalize(name.value);
    });
    canonical.enableWhen = () => state == stateCreate;
  }

  void _load(RouteProvider routeProvider) {
    loaded = false;
    companyService.find(routeProvider.parameters['canonical']).then((_) {
      company = _.toCompanyForm();
      app.breadCrumb.path.last.name = formTitle;
      loaded = true;
    });
  }

  Future<HttpResult> onSubmit(CompanyForm data) {
    if (state == stateCreate) {
      return companyService.create(data);
    } else {
      return companyService.update(data);
    }
  }

  Future onDone(HttpResult httpResult) {
    router.gotoUrl('/companies');
    return new Future.value();
  }
}